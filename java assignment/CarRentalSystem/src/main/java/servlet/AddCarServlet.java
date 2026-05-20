package servlet;

import dao.CarDAO;
import model.Car;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FilterOptions;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import utils.CSRFUtil;
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
public class AddCarServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AddCarServlet.class);
    private CarDAO carDAO;

    @Override
    public void init() {
        carDAO = new CarDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Add predefined filter options
        CSRFUtil.ensureToken(request.getSession());
        setFilterAttributes(request);
        request.getRequestDispatcher("/addCar.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!CSRFUtil.isValid(request)) {
            ServletUtil.redirectWithError(response, request.getContextPath() + "/admin/addCar", "Invalid security token");
            return;
        }

        String brand = request.getParameter("brand");
        String model = request.getParameter("model");
        String category = request.getParameter("category");
        String fuelType = request.getParameter("fuelType");
        String location = request.getParameter("location");
        String priceStr = request.getParameter("pricePerDay");
        String imageUrl = request.getParameter("imageUrl");
        String status = request.getParameter("status");

        request.setAttribute("brand", brand);
        request.setAttribute("model", model);
        request.setAttribute("category", category);
        request.setAttribute("fuelType", fuelType);
        request.setAttribute("location", location);
        request.setAttribute("pricePerDay", priceStr);
        request.setAttribute("imageUrl", imageUrl);
        request.setAttribute("status", status);

        double pricePerDay;
        try {
            pricePerDay = Double.parseDouble(priceStr);
            if (pricePerDay <= 0) {
                throw new NumberFormatException("Price must be positive");
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid price format: " + priceStr, e);
            request.setAttribute("errorMessage", "Price per day must be a valid positive number.");
            setFilterAttributes(request);
            request.getRequestDispatcher("/addCar.jsp").forward(request, response);
            return;
        }

        if (!ValidationUtil.isNotEmpty(brand) || !ValidationUtil.isNotEmpty(model) || 
            !ValidationUtil.isNotEmpty(category) || !ValidationUtil.isNotEmpty(fuelType) || 
            !ValidationUtil.isNotEmpty(location)) {
            request.setAttribute("errorMessage", "All required fields must be filled.");
            setFilterAttributes(request);
            request.getRequestDispatcher("/addCar.jsp").forward(request, response);
            return;
        }
        
        if (carDAO.carExists(brand.trim(), model.trim(), fuelType.trim(), location.trim())) {
            request.setAttribute("errorMessage", "A car with same brand, model, fuel type and location already exists.");
            setFilterAttributes(request);
            request.getRequestDispatcher("/addCar.jsp").forward(request, response);
            return;
        }

        Part imagePart = request.getPart("imageFile");
        if (imagePart != null && imagePart.getSize() > 0) {
            imageUrl = saveUploadedImage(imagePart, request);
        }

        if (!ValidationUtil.isNotEmpty(imageUrl)) {
            request.setAttribute("errorMessage", "Provide an image URL or upload an image file.");
            setFilterAttributes(request);
            request.getRequestDispatcher("/addCar.jsp").forward(request, response);
            return;
        }
        if (!isValidImagePath(imageUrl)) {
            request.setAttribute("errorMessage", "Image must be a valid URL (http/https) or uploaded image path.");
            setFilterAttributes(request);
            request.getRequestDispatcher("/addCar.jsp").forward(request, response);
            return;
        }

        Car car = new Car(0, brand.trim(), model.trim(), category.trim(), pricePerDay,
                !ValidationUtil.isNotEmpty(status) ? "AVAILABLE" : status.trim(), imageUrl.trim(), fuelType.trim(), location.trim());

        if (carDAO.addCar(car)) {
            ServletUtil.redirectWithSuccess(response, request.getContextPath() + "/admin/cars", "Car added successfully");
        } else {
            String reason = carDAO.getLastErrorMessage();
            request.setAttribute("errorMessage", "Failed to add car" + (reason != null ? ": " + reason : "."));
            setFilterAttributes(request);
            request.getRequestDispatcher("/addCar.jsp").forward(request, response);
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
        String rootPath = request.getServletContext().getRealPath("/");
        if (rootPath == null) {
            rootPath = request.getServletContext().getRealPath("");
        }
        
        String uploadDir;
        if (rootPath != null) {
            java.io.File rootFile = new java.io.File(rootPath);
            uploadDir = new java.io.File(rootFile, "uploads").getAbsolutePath();
        } else {
            uploadDir = System.getProperty("java.io.tmpdir") + java.io.File.separator + "CarRentalSystem" + java.io.File.separator + "uploads";
        }
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        try (InputStream input = part.getInputStream()) {
            Files.copy(input, uploadPath.resolve(storedName), StandardCopyOption.REPLACE_EXISTING);
        }
        return "uploads/" + storedName;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
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
}
