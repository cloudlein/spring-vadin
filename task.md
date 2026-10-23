# TOTP & Mandatory MFA on Logout Implementation Roadmap

Technical task backlog and engineering specification for implementing Time-based One-Time Password (TOTP) Two-Factor Authentication with the business rule: **Users are required to enable TOTP prior to logging out**.

---

## Definition of Done (DoD)
1. Newly registered users have `isTotpEnabled = false` by default.
2. When a user requests `/api/auth/logout`, the system validates the user's TOTP status:
   - If `false` or `null`: The logout request is rejected with HTTP `400 Bad Request` and an explicit error message prompting TOTP activation.
   - If `true`: The token is blacklisted and the logout completes successfully (HTTP `200 OK`).
3. Authenticated users can request TOTP provisioning parameters (Secret Key and QR Code).
4. Authenticated users can verify a 6-digit OTP code to confirm and activate TOTP (`isTotpEnabled = true`).
5. Subsequent login attempts for MFA-enabled accounts:
   - Primary credential validation returns `mfaRequired: true` along with an ephemeral `tempToken`.
   - The TOTP verification endpoint exchanges the `tempToken` and valid 6-digit code for fully authenticated access and refresh tokens.

---

## Task Checklist

### Phase 1: Core TOTP Engine & Utilities
- [ ] **Task 1.1: Create TOTP Setup Response DTO**
  - **Proposed Location**: `com.java.springvadin.dto.auth.TotpSetupResponse`
  - **Requirements**: Record or class containing:
    - `secretKey` (String): Raw Base32 secret for manual entry in authenticator applications.
    - `qrCodeDataUrl` (String): Base64-encoded image Data URI (e.g., `data:image/png;base64,...`) for direct rendering in an `<img>` tag.
    - `manualUri` (String): Standard RFC 6238 URI format (`otpauth://totp/...`).

- [ ] **Task 1.2: Create TOTP Enable Request DTO**
  - **Proposed Location**: `com.java.springvadin.dto.auth.TotpEnableRequest`
  - **Requirements**: Contains field `code` (String / 6-digit OTP) with Bean Validation constraints (`@NotBlank`, `@Size(min = 6, max = 6)`).

- [ ] **Task 1.3: Create `TotpService` Interface & Implementation**
  - **Proposed Location**: `com.java.springvadin.service.TotpService` & `service.impl.TotpServiceImpl`
  - **Method Specifications**:
    1. `String generateSecretKey()`:
       - Generate a cryptographically secure Base32-encoded random secret (minimum 160 bits / 20–32 bytes).
    2. `String generateQrCodeDataUri(String secret, String accountName, String issuer)`:
       - Construct standard TOTP URI: `otpauth://totp/{issuer}:{accountName}?secret={secret}&issuer={issuer}&algorithm=SHA1&digits=6&period=30`.
       - Render PNG byte stream using `com.github.kenglxn.QRGen.javase.QRCode`.
       - Encode to Base64 Data URI: `"data:image/png;base64," + Base64...`.
    3. `boolean verifyCode(String secret, String code)`:
       - Utilize `TimeBasedOneTimePasswordGenerator` from `com.eatthepath.otp.TimeBasedOneTimePasswordGenerator`.
       - Decode the Base32 secret into a `SecretKey`.
       - Validate the code against current system timestamp (`Instant.now()`), allowing standard clock drift tolerance (±1 period / 30 seconds).

---

### Phase 2: Ephemeral MFA Token Management
- [ ] **Task 2.1: Implement Ephemeral MFA Token Generator in `JwtService`**
  - **Target File**: [JwtService.java](file:///home/udin/projects/spring-vadin/src/main/java/com/java/springvadin/service/JwtService.java)
  - **Requirements**:
    - Method `generateMfaToken(UserDetails userDetails)` or `generateMfaToken(String username)`.
    - Include a distinct claim to indicate token purpose: `claims.put("type", "MFA_TEMP")`.
    - Set short time-to-live (e.g., 3 to 5 minutes).

- [ ] **Task 2.2: Enforce Token Isolation in `JwtAuthenticationFilter`**
  - **Target File**: [JwtAuthenticationFilter.java](file:///home/udin/projects/spring-vadin/src/main/java/com/java/springvadin/config/JwtAuthenticationFilter.java)
  - **Requirements**:
    - Ensure regular request authentication filters verify token claims.
    - Explicitly reject tokens containing claim `type == "MFA_TEMP"` from establishing authenticated sessions for standard protected resources.

---

### Phase 3: Business Logic & Service Layer (`AuthService`)
- [ ] **Task 3.1: Implement TOTP Provisioning Logic**
  - **Method**: `TotpSetupResponse setupTotp(String username)`
  - **Execution Flow**:
    1. Retrieve user by `username`.
    2. Check if a secret already exists or generate a new one if not enabled yet.
    3. Persist the generated secret to the user record (`isTotpEnabled` remains `false`).
    4. Return `TotpSetupResponse` containing QR Data URI and raw secret.

- [ ] **Task 3.2: Implement TOTP Activation Logic**
  - **Method**: `void enableTotp(String username, String code)`
  - **Execution Flow**:
    1. Fetch the user's secret key from persistence.
    2. Validate the submitted code via `totpService.verifyCode(...)`.
    3. If invalid: throw a client error (e.g., `BadRequestException("Invalid TOTP verification code")`).
    4. If valid: update `user.setIsTotpEnabled(true)` and persist changes.

- [ ] **Task 3.3: Implement Mandatory TOTP Pre-condition on Logout**
  - **Method**: `void logout(String token)` in [AuthServiceImpl.java](file:///home/udin/projects/spring-vadin/src/main/java/com/java/springvadin/service/impl/AuthServiceImpl.java)
  - **Execution Flow**:
    1. Extract the user identity from the provided bearer token.
    2. Inspect user status `user.getIsTotpEnabled()`.
    3. **Enforcement Rule**:
       - If `isTotpEnabled == null || !isTotpEnabled`: throw `BadRequestException("TOTP Two-Factor Authentication must be activated before logging out.")`.
       - If `true`: proceed with token blacklisting in Redis and clear `SecurityContextHolder`.

- [ ] **Task 3.4: Adapt Two-Step Authentication in Login Flow**
  - **Method**: `AuthResponse login(LoginRequest loginRequest)` in [AuthServiceImpl.java](file:///home/udin/projects/spring-vadin/src/main/java/com/java/springvadin/service/impl/AuthServiceImpl.java)
  - **Execution Flow**:
    1. Authenticate username and password credentials using `authenticationManager`.
    2. Evaluate flag `user.getIsTotpEnabled()`.
    3. If `false` or `null`: issue standard access and refresh tokens.
    4. If `true`:
       - Suppress full access token issuance.
       - Generate ephemeral token via `jwtService.generateMfaToken(...)`.
       - Return `AuthResponse` with `mfaRequired: true` and `tempToken: "..."`.

- [ ] **Task 3.5: Implement TOTP Verification Endpoint Logic**
  - **Method**: `AuthResponse verifyTotpLogin(TotpVerifyRequest request)`
  - **Execution Flow**:
    1. Validate `request.tempToken()` (signature, claim `type == "MFA_TEMP"`, non-expired).
    2. Extract username from `tempToken`.
    3. Retrieve user entity and associated `totpSecret`.
    4. Validate the 6-digit code via `totpService.verifyCode(...)`.
    5. If valid: issue full access token and refresh token in `AuthResponse`.

---

### Phase 4: Controller Endpoints & Security Configuration
- [ ] **Task 4.1: Expose REST Endpoints in `AuthController`**
  - **Target File**: [AuthController.java](file:///home/udin/projects/spring-vadin/src/main/java/com/java/springvadin/controller/AuthController.java)
  - Endpoints to declare:
    - `GET /api/auth/totp/setup` (Authenticated)
    - `POST /api/auth/totp/enable` (Authenticated, Body: `TotpEnableRequest`)
    - `POST /api/auth/totp/verify` (Public, Body: `TotpVerifyRequest`)

- [ ] **Task 4.2: Update Spring Security Filter Chain**
  - **Target File**: [SecurityConfig.java](file:///home/udin/projects/spring-vadin/src/main/java/com/java/springvadin/config/SecurityConfig.java)
  - Configure route authorization:
    - Allow unauthenticated access (`permitAll()`) to `POST /api/auth/totp/verify`.
    - Require active authentication (`authenticated()`) for `GET /api/auth/totp/setup` and `POST /api/auth/totp/enable`.

---

### Phase 5: Verification & Quality Assurance (Testing Guide)

- [ ] **Test Case 1: Initial Registration & Login**
  - Register a new user -> Execute primary login.
  - Verification: Standard response containing full `accessToken`; `mfaRequired` is `false` or omitted.
- [ ] **Test Case 2: Attempt Immediate Logout (Expected Failure)**
  - Invoke `POST /api/auth/logout` using the active Bearer token.
  - Verification: HTTP `400 Bad Request` returned with required TOTP message. Token remains valid and unblacklisted.
- [ ] **Test Case 3: Provision TOTP Secret & QR Code**
  - Invoke `GET /api/auth/totp/setup` with the Bearer token.
  - Verification: HTTP `200 OK` with Base64 QR code and secret string. Import key into Google Authenticator or Authy.
- [ ] **Test Case 4: Enable TOTP with Invalid vs Valid Code**
  - Invoke `POST /api/auth/totp/enable` with invalid code `000000` -> Expect HTTP `400 Bad Request`.
  - Invoke with valid 6-digit code from authenticator app -> Expect HTTP `200 OK`.
- [ ] **Test Case 5: Retry Logout (Expected Success)**
  - Invoke `POST /api/auth/logout` with Bearer token.
  - Verification: HTTP `200 OK`. Token is blacklisted in Redis.
- [ ] **Test Case 6: Login Flow with MFA Enforced**
  - Invoke `POST /api/auth/login`.
  - Verification: Returns `mfaRequired: true` and `tempToken`; no standard access token provided.
  - Invoke `POST /api/auth/totp/verify` with `tempToken` and current 6-digit code.
  - Verification: Successfully returns full `accessToken` and `refreshToken`.
