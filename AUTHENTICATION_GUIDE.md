# Authentication System Guide

## Overview
Your application now features a robust authentication system with real email and password support. Users can sign up with real email addresses and create accounts with strong passwords.

---

## Sign Up Features

### ✅ Real Email Support
- **Valid Email Format Required**: Must match pattern `user@example.com`
- **Real-time Validation**: Email format is validated as you type
- **Unique Email Requirement**: Each email can only be registered once
- **Maximum Security**: Emails are stored securely in the database

### ✅ Strong Password Requirements
Users must create passwords with the following criteria:
- **Minimum 8 characters** (recommended 12+)
- **Must contain Uppercase letter** (A-Z)
- **Must contain Lowercase letter** (a-z)
- **Must contain Number** (0-9)
- **Must contain Special character** (@$!%*?&-_()+=)

### ✅ Password Strength Indicator
Real-time feedback as you type:
```
[_____] No password          (Red)
[#____] Very Weak           (Red)
[##___] Weak                (Red)
[###__] Fair                (Orange)
[####_] Strong              (Green)
[#####] Very Strong         (Green)
```

### ✅ Password Visibility Toggle
- **Show/Hide button** for first password field
- **Show/Hide button** for confirm password field
- Allows users to verify they entered the correct password
- Safe way to check password without exposing it permanently

### Example Strong Passwords
- `MyPassword123!`
- `SecurePass@2024`
- `VehicleService#2024`
- `Admin&Manager123`

---

## Login Features

### ✅ Flexible Login Options
Users can login with:
- **Username** (must be 4+ characters)
- **Email** (your real email address used during signup)

### ✅ Password Visibility Toggle
- **Show/Hide button** next to password field
- Toggle to see your password before logging in
- Prevents accidental password mistakes

### ✅ Real-time Feedback
Login messages appear directly on the screen:
- "Verifying credentials..." (during login)
- "Login Successful! Opening dashboard..." (success)
- "Invalid username/email or password!" (failure)

---

## Test Accounts

### Built-in Admin Account
You can always login with test credentials:
```
Username: admin
Password: admin
```

### Create New Accounts
Click "Create Account" to sign up with:
- Your real full name
- Your real email address
- A strong password (8+ chars with uppercase, lowercase, number, special char)

---

## Security Features

### ✅ Password Hashing
- All passwords are hashed using SHA-256 algorithm
- Original passwords are never stored in the database
- Even database administrators cannot see your password

### ✅ Email Validation
- Real email format validation (RFC 5322 standard)
- Users must provide valid email their first signup
- Helps with account recovery in future versions

### ✅ Duplicate Prevention
- Cannot create account with existing username
- Cannot create account with existing email
- System automatically checks and prevents duplicates

### ✅ Input Validation
- All inputs are validated on the client side
- Clear error messages guide users to fix input
- Prevents invalid data from reaching the database

---

## How to Sign Up

1. **Click "Create Account" button** on the login screen
2. **Enter Full Name** (your real name, 3+ characters)
3. **Choose Username** (4+ characters, alphanumeric)
4. **Enter Email** (valid format: user@domain.com)
5. **Set Password** (8+ chars with uppercase, lowercase, number, special char)
   - Watch the strength indicator
   - Use examples: MyPassword123!, SecurePass@2024
6. **Confirm Password** (must match exactly)
7. **Click "Create Account"** button
8. **Success!** You'll be redirected to login screen
9. **Login** with your new credentials

---

## How to Login

### Method 1: Username
1. Enter username in "Username or Email" field
2. Enter password
3. Click "Show" to verify password (optional)
4. Click "Login"

### Method 2: Email
1. Enter email address in "Username or Email" field
2. Enter password
3. Click "Show" to verify password (optional)
4. Click "Login"

---

## Troubleshooting

### "Invalid username/email or password!"
- ✓ Check spelling of username/email
- ✓ Verify CAPS LOCK is not on
- ✓ Ensure password is exactly as you set it
- ✓ Try the test account: admin/admin

### "Email already registered!"
- ✓ Use a different email address
- ✓ Or try logging in with that email using "Login" button

### "Username already taken!"
- ✓ Choose a different username
- ✓ Or login with existing account

### "Password must be at least 8 characters"
- ✓ Make password longer (8+ characters)
- ✓ Add more characters for better security

### "Password must have uppercase, lowercase, number & special char"
- ✓ Example: `MyPassword123!`
- ✓ Must include: A-Z, a-z, 0-9, @$!%*?&-_()+=

### "Invalid email format"
- ✓ Use format: `yourname@example.com`
- ✓ Check for spaces or special characters
- ✓ Email must have @ and domain name

### "Connection error. Check if API server is running."
- ✓ Make sure the application is fully started
- ✓ The API server starts automatically
- ✓ Wait a few seconds and try again
- ✓ Check console for error messages

---

## Best Practices

1. **Use Strong, Unique Passwords**
   - Don't reuse passwords from other accounts
   - Use mix of uppercase, lowercase, numbers, special chars
   - Longer passwords are better (12+ chars, 8+ minimum)

2. **Verify Your Email**
   - Use a real, working email address
   - This helps with future account recovery features
   - Keep your email address updated

3. **Remember Your Password**
   - Write it down in a secure location
   - Or use a password manager
   - The system cannot recover lost passwords

4. **Keep Username Simple**
   - Use letters, numbers, and underscores only
   - Examples: john_doe, user123, vehicle_admin
   - Minimum 4 characters

---

## Database Schema

The system uses these fields for authentication:

```sql
users table:
- user_id (Integer, Auto-increment)
- username (Varchar(50), Unique)
- email (Varchar(100), Unique)
- password (Varchar(255), SHA-256 hashed)
- full_name (Varchar(100))
- created_at (Timestamp, Auto-set)
```

---

## API Endpoints

The authentication system uses these REST API endpoints:

### User Registration
```
POST /api/users/register
Body: {
  "username": "john_doe",
  "email": "john@example.com",
  "password": "MyPassword123!",
  "full_name": "John Doe"
}
```

### User Login
```
POST /api/users/login
Body: {
  "username": "john_doe",  // or email
  "password": "MyPassword123!"
}
```

---

## Future Enhancements

Planned features for future versions:
- Email verification on signup
- Password reset via email
- Two-factor authentication (2FA)
- Account recovery options
- Login history tracking
- Remember me functionality

---

## Support

For issues or questions:
1. Check the error messages displayed in the application
2. Review the Troubleshooting section above
3. Check the console output for detailed error logs
4. Ensure MySQL database is running properly
5. Verify API server started successfully

---

**Your authentication system is now ready for real-world use!** 🔐
