# 🚀 How to Run RAIG-Java Web Interface

## Quick Start (Easiest Method)

### Option 1: Using the Startup Script (Recommended)

1. **Double-click** `start-server.ps1` in File Explorer
   - Or right-click → "Run with PowerShell"

2. **Wait** for compilation (takes ~10 seconds)

3. **Open your browser** and go to:
   ```
   http://localhost:8080
   ```

4. **Stop the server**: Press `Ctrl+C` in the PowerShell window

---

## Manual Methods

### Option 2: Using PowerShell Commands

```powershell
# 1. Set Java environment
$env:JAVA_HOME = "C:\Program Files\Java\jdk-23"

# 2. Navigate to project
cd "c:\Users\TECQNIO\OneDrive\Documents\GitHub\RAIG-Java"

# 3. Compile
mvn compile -q

# 4. Run the server
java -cp "target/classes;$HOME\.m2\repository\org\slf4j\slf4j-api\2.0.9\slf4j-api-2.0.9.jar;$HOME\.m2\repository\ch\qos\logback\logback-classic\1.4.14\logback-classic-1.4.14.jar;$HOME\.m2\repository\ch\qos\logback\logback-core\1.4.14\logback-core-1.4.14.jar" Main
```

### Option 3: Using Maven (Alternative)

```powershell
# 1. Set Java environment
$env:JAVA_HOME = "C:\Program Files\Java\jdk-23"

# 2. Navigate to project
cd "c:\Users\TECQNIO\OneDrive\Documents\GitHub\RAIG-Java"

# 3. Compile and run Main.java directly
mvn compile
java -cp target/classes Main
```

---

## 🌐 Accessing the Web Interface

Once the server starts, you'll see:
```
==============================================
RAIG Framework Web Interface Started
==============================================
Access the website at: http://localhost:8080
```

**Open your browser** and navigate to:
- **Homepage:** http://localhost:8080
- **API - 7 Pillars:** http://localhost:8080/api/pillars
- **API - Scenarios:** http://localhost:8080/api/scenarios

---

## 🎯 What You'll See

The web interface includes:

1. **Seven Pillars Display** - Visual cards for each AI ethics dimension
2. **Interactive Demo** - 5 pre-configured scenarios to test
3. **Manual Evaluation** - Form to submit custom AI decisions
4. **Real-time Results** - Live evaluation with violations/warnings
5. **Three-State System:**
   - 🟢 **APPROVE** - Decision passes all checks
   - 🔴 **BLOCK** - Violations detected
   - 🟡 **ESCALATE** - Requires human review

---

## 🧪 Try the Demo Scenarios

Click on any scenario in the web interface:

1. **Clean Decision** → Should APPROVE ✅
2. **Privacy Violation** → Should BLOCK ❌
3. **High Bias** → Should BLOCK ❌
4. **Missing Explanation** → Should BLOCK ❌
5. **Borderline Confidence** → Should ESCALATE ⚠️

---

## 📊 Testing the APIs

### Using PowerShell (while server is running):

**Get 7 Pillars:**
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/pillars" -UseBasicParsing | Select-Object -ExpandProperty Content
```

**Get Scenarios:**
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/scenarios" -UseBasicParsing | Select-Object -ExpandProperty Content
```

**Evaluate a Decision:**
```powershell
$body = @{
    decisionLabel = "Loan Approved"
    confidence = "0.92"
    responsibleEntity = "CreditModel_v1"
    explanation = "Applicant met all criteria"
    userName = "Alice"
    userEmail = "alice@bank.com"
    hasConsent = "true"
    hasSensitiveData = "false"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/evaluate" -Method POST -Body $body -ContentType "application/json" -UseBasicParsing | Select-Object -ExpandProperty Content
```

---

## ⚠️ Troubleshooting

### Server won't start?

**Check Java is set correctly:**
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-23"
java -version
```
Should show: `java version "23"`

**Recompile the project:**
```powershell
mvn clean compile
```

### Port 8080 already in use?

**Find what's using port 8080:**
```powershell
Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
```

**Kill the process:**
```powershell
Stop-Process -Id <ProcessId> -Force
```

### Browser shows "Can't connect"?

1. Make sure the server is running (you should see the startup message)
2. Check the URL is exactly: `http://localhost:8080`
3. Try: `http://127.0.0.1:8080`

---

## 🛑 Stopping the Server

**In the PowerShell window where the server is running:**
- Press `Ctrl+C`
- Type `Y` and press `Enter` if asked to confirm

---

## 📝 Quick Reference

| Action | Command |
|--------|---------|
| Start server | Run `start-server.ps1` |
| View homepage | http://localhost:8080 |
| Stop server | `Ctrl+C` in PowerShell |
| Recompile | `mvn clean compile` |
| Check tests | `mvn test` |

---

## 🎉 You're All Set!

The RAIG-Java web interface is now ready to use. Enjoy exploring the Responsible AI Governance framework!

**Need help?** Check [README.md](README.md) or [FINAL_VERIFICATION.md](FINAL_VERIFICATION.md)
