# Quick Start - Real Email & Password Setup

## 📋 Checklist Before You Start

- [ ] MySQL Database is running
- [ ] Database schema has been executed
- [ ] Application compiled without errors
- [ ] You have a real email address ready

---

## 🚀 First Time User - Complete Setup

### Step 1: Start the Application
```bash
# Windows
.\run.bat

# Mac/Linux
./run.sh
```

Wait for the login screen to appear (~5 seconds).

### Step 2: Create Your Account
1. Click **"Create Account"** button
2. Fill in your information:
   - **Full Name**: John Doe
   - **Username**: john_doe (4+ chars, alphanumeric)
   - **Email**: john@gmail.com (real email address)
   - **Password**: MyPassword123! (must be strong)
   - **Confirm**: MyPassword123! (must match)
3. Click **"Create Account"**
4. If successful, you'll see green message and be returned to login

### Step 3: Login with Your Account
1. Enter username or email: `john_doe` OR `john@gmail.com`
2. Enter password: `MyPassword123!`
3. Click **"Show"** button to verify password if needed
4. Click **"Login"**
5. Welcome! You're now logged in

---

## ✅ Password Requirements Checklist

Valid Password Example: **`MyPassword123!`**

Your password MUST have ALL of these:
```
[✓] Uppercase letter (A-Z)        - Has "M" and "P"
[✓] Lowercase letter (a-z)        - Has "y", "a", "s", etc
[✓] Number (0-9)                  - Has "1", "2", "3"
[✓] Special character (@$!%*?&)   - Has "!"
[✓] At least 8 characters         - Has 14 characters
```

---

## 📧 Email Format Examples

**Valid Emails** ✅
- john@gmail.com
- jane.doe@company.com
- user_123@example.org
- name+tag@domain.co.uk

**Invalid Emails** ❌
- john (no @ symbol)
- john@ (no domain)
- john@.com (no domain name)
- john @email.com (space in middle)

---

## 🔐 Strong Password Examples

### Great Passwords (8-12 characters)
1. `TechPass2024!`
2. `MyVehicle#2024`
3. `Admin@Service123`
4. `SecureCode$2024`
5. `VehicleApp!99`

### Excellent Passwords (12+ characters)
1. `MyServiceCenter#2024!`
2. `VehicleManagement_App123`
3. `SecurePassword$123!2024`
4. `CompanyService@App#2024`

### What Makes a Password Strong
```
MyPassword123!
│    │       │ │
│    │       │ └─> Special character
│    │       └───> Number
│    └───────────> Lowercase
└────────────────> Uppercase
```

---

## 🎯 Common Scenarios

### Scenario 1: New User
```
Register:
- Email: yourname@gmail.com
- Password: YourPass123!
- Username: yourname

Login:
- Username or Email: yourname (or yourname@gmail.com)
- Password: YourPass123!
```

### Scenario 2: Lost Password
Currently: Create a new account with different email
Future: Password reset feature coming soon

### Scenario 3: Multiple Users on Same Computer
Each person registers with their own email and account:
```
User 1: john@gmail.com - john_doe - JohnsPass123!
User 2: jane@gmail.com - jane_doe - JanesPass456@
User 3: bob@gmail.com - bob_smith - BobPass789#
```

---

## 🔧 Testing Your Setup

### Test with Built-in Account
No need to register first, use:
```
Username: admin
Password: admin
```

### Test with Real Email
1. Create account with real email
2. Logout (or close and restart)
3. Login with that email address
4. Verify it works!

---

## 📱 Important Notes

### About Your Email
- Use a REAL email address (you control)
- System validates email format
- Unique - each email can only register once
- Future versions will use email for password reset

### About Your Password
- Passwords are HASHED (one-way encryption)
- Not even admins can see your password
- If you forget it: create new account with different email
- Change behavior: create new account with new password

### About Your Username
- Username must be 4+ characters
- Can contain letters, numbers, underscores
- Is UNIQUE - no two users can have same username
- Case-sensitive (john_doe ≠ John_Doe)

---

## 🚨 Error Messages & Solutions

| Error Message | Cause | Solution |
|---|---|---|
| "Invalid email format" | Wrong email format | Use: name@domain.com |
| "Email already registered" | Email taken | Use different email |
| "Username already taken" | Username taken | Choose different username |
| "Password must be 8 chars" | Password too short | Make password longer |
| "Password must have uppercase..." | Missing character types | Add capital letter, number, special char |
| "Invalid username or password" | Wrong credentials | Check spelling and CAPS LOCK |
| "Connection error" | API not running | Check server startup |

---

## 🎓 Learning Path

1. **Start**: Test with admin account (admin/admin)
2. **Practice**: Create a real account
3. **Explore**: Add customers and vehicles
4. **Master**: Manage all aspects of service system

---

## 💡 Pro Tips

✅ **DO THIS:**
- Use memorable but strong password
- Save your password securely
- Use email you actually own
- Verify your full name is spelled correctly
- Keep uppercase/lowercase consistent

❌ **DON'T DO THIS:**
- Use simple passwords like "123456" or "password"
- Reuse passwords from other sites
- Share your password with others
- Use same email as other important accounts
- Forget your password without backup

---

## 📞 Need Help?

1. **Check error message** - It tells you exactly what's wrong
2. **Verify email format** - Must have @ and domain
3. **Check password** - Must have UPPER, lower, number, special char
4. **Test admin account** - Use admin/admin to verify system works
5. **Review examples** - Follow the password examples above

---

**You're all set! Create your account and start managing your vehicle service business!** 🚗✨
