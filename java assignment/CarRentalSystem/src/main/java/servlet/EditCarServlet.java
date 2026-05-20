package servlet;

import dao.CarDAO;
import model.Car;
import utils.FilterOptions;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CSRFUtil;
import utils.SessionUtil;
import utils.ValidationUtil;
import utils.ServletUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@MultipartConfig
public class EditCarServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(EditCarServlet.class);
    private CarDAO carDAO;

    @Override
    public void init() {
        carDAO = new CarDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            logger.error("Invalid car id in doGet: {}", request.getParameter("id"), e);
            ServletUtil.redirectWithError(response, request.getContextPath() + "/admin/cars", "Invalid car id");
            return;
        }
        Car car = carDAO.getCarById(id);
        if (car == null) {
            ServletUtil.redirectWithError(response, request.getContextPath() + "/admin/cars", "Car not found");
            return;
        }
        request.setAttribute("car", car);
        CSRFUtil.ensureToken(request.getSession());
        setFilterAttributes(request);
        request.getRequestDispatcher("/editCar.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!CSRFUtil.isValid(request)) {
            ServletUtil.redirectWithError(response, request.getContextPath() + "/admin/cars", "Invalid security token");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            logger.error("Invalid car id in doPost: {}", request.getParameter("id"), e);
            ServletUtil.redirectWithError(response, request.getContextPath() + "/admin/cars", "Invalid car id");
            return;
        }

        String brand = request.getParameter("brand");
        String model = request.getParameter("model");
        String category = request.getParameter("category");
        String fuelType = request.getParameter("fuelType");
        String location = request.getParameter("location");
        String priceStr = request.getParameter("pricePerDay");
        String status = request.getParameter("status");
        String imageUrl = request.getParameter("imageUrl");
        double pricePerDay;

        try {
            pricePerDay = Double.parseDouble(priceStr);
            if (pricePerDay <= 0) {
                throw new NumberFormatException("Price must be positive");
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid price per day: {}", priceStr, e);
            request.setAttribute("errorMessage", "Price per day must be a valid positive number.");
            request.setAttribute("car", buildCarFromRequest(id, brand, model, category, fuelType, location, status, imageUrl, 0));
            setFilterAttributes(request);
            request.getRequestDispatcher("/editCar.jsp").forward(request, response);
            return;
        }

        if (!ValidationUtil.isNotEmpty(brand) || !ValidationUtil.isNotEmpty(model) || 
            !ValidationUtil.isNotEmpty(category) || !ValidationUtil.isNotEmpty(fuelType) || 
            !ValidationUtil.isNotEmpty(location)) {
            request.setAttribute("errorMessage", "All required fields must be filled.");
            request.setAttribute("car", buildCarFromRequest(id, brand, model, category, fuelType, location, status, imageUrl, pricePerDay));
            setFilterAttributes(request);
            request.getRequestDispatcher("/editCar.jsp").forward(request, response);
            return;
        }
        
        if (carDAO.carExistsForUpdate(id, brand.trim(), model.trim(), fuelType.trim(), location.trim())) {
            request.setAttribute("errorMessage", "Another car with same brand, model, fuel type and location already exists.");
            request.setAttribute("car", buildCarFromRequest(id, brand, model, category, fuelType, location, status, imageUrl, pricePerDay));
            setFilterAttributes(request);
            request.getRequestDispatcher("/editCar.jsp").forward(request, response);
            return;
        }

        Part imagePart = request.getPart("imageFile");
        if (imagePart != null && imagePart.getSize() > 0) {
            imageUrl = saveUploadedImage(imagePart, request);
        }
        
        if (!ValidationUtil.isNotEmpty(imageUrl)) {
            request.setAttribute("errorMessage", "Provide an image URL or upload an image file.");
            request.setAttribute("car", buildCarFromRequest(id, brand, model, category, fuelType, location, status, imageUrl, pricePerDay));
            setFilterAttributes(request);
            request.getRequestDispatcher("/editCar.jsp").forward(request, response);
            return;
        }
        
        if (!isValidImagePath(imageUrl)) {
            request.setAttribute("errorMessage", "Image must be a valid URL (http/https) or uploaded image path.");
            request.setAttribute("car", buildCarFromRequest(id, brand, model, category, fuelType, location, status, imageUrl, pricePerDay));
            setFilterAttributes(request);
            request.getRequestDispatcher("/editCar.jsp").forward(request, response);
            return;
        }

        Car car = new Car(id, brand.trim(), model.trim(), category.trim(), pricePerDay, status, imageUrl.trim(), fuelType.trim(), location.trim());

        if (carDAO.updateCar(car)) {
            ServletUtil.redirectWithSuccess(response, request.getContextPath() + "/admin/cars", "Car updated successfully");
        } else {
            String reason = carDAO.getLastErrorMessage();
            request.setAttribute("errorMessage", "Failed to update car" + (reason != null ? ": " + reason : "."));
            request.setAttribute("car", car);
            setFilterAttributes(request);
            request.getRequestDispatcher("/editCar.jsp").forward(request, response);
        }
    }

    private void setFilterAttributes(HttpServletRequest request) {
        request.setAttribute("categories", FilterOptions.getCategories());
        request.setAttribute("fuelTypes", FilterOptions.getFuelTypes());
        request.setAttribute("locations", FilterOptions.getLocations());
        request.setAttribute("statuses", FilterOptions.getCarStatus());
    }

    private String saveUploadedImage(Part part, HttpServletRequest request) throws IOException {
        String submittedFileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        if (submittedFileName == null || submittedFileName.isBlank()) {
            return null;
        }
        String extension = "";
        int idx = submittedFileName.lastIndexOf('.');
        if (idx >= 0) {
            extension = submittedFileName.substring(idx);
        }
        String storedName = "car-" + UUID.randomUUID() + extension;
        String rootPath = request.getServletContext().getRealPath("");
        String uploadDir = (rootPath != null)
                ? rootPath + "uploads"
                : System.getProperty("java.io.tmpdir") + java.io.File.separator + "CarRentalSystem" + java.io.File.separator + "uploads";
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        try (InputStream input = part.getInputStream()) {
            Files.copy(input, uploadPath.resolve(storedName), StandardCopyOption.REPLACE_EXISTING);
        }
        return "uploads/" + storedName;
    }

    private boolean isValidImagePath(String path) {
        if (path == null || path.isBlank()) {
            return false;
        }
        String value = path.trim().toLowerCase();
        
        // Allowed extensions
        String[] extensions = {".jpg", ".jpeg", ".png", ".webp", ".gif"};
        
        boolean hasValidExtension = false;
        for (String ext : extensions) {
            if (value.matches(".*\\" + ext + "(\\?.*)?$")) {
                hasValidExtension = true;
                break;
            }
        }

        if (value.startsWith("uploads/")) {
            return hasValidExtension;
        }

        if (value.startsWith("http://") || value.startsWith("https://")) {
            // Reject known indirect/redirector patterns
            if (value.contains("google.com/imgres") || value.contains("images.google") || 
                value.contains("search?") || value.contains("url?") || value.contains("imgurl=")) {
                return false;
            }
            return hasValidExtension;
        }
        
        return false;
    }

    private Car buildCarFromRequest(int id, String brand, String model, String category, String fuelType, String location,
                                    String status, String imageUrl, double pricePerDay) {
        return new Car(
                id,
                brand == null ? "" : brand,
                model == null ? "" : model,
                category == null ? "" : category,
                pricePerDay,
                status == null ? "AVAILABLE" : status,
                imageUrl == null ? "" : imageUrl,
                fuelType == null ? "" : fuelType,
                location == null ? "" : location
        );
    }
}
