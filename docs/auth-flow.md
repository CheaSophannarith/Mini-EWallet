# Authentication Flow — eWallet

---

## 1. Login Flow

### Endpoint
```
POST /api/v1/auth/login
```

### Request Body
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

### What happens step by step

```
Client
  │
  │  POST /api/v1/auth/login  { email, password }
  ▼
JwtAuthFilter
  │
  │  No Authorization header → skip filter, pass through
  ▼
AuthController.login()
  │
  │  @Valid @RequestBody → validates email format + password length
  ▼
AuthServiceImpl.login()
  │
  ├─ 1. Find user by email in DB
  │       └─ Not found → 401 UsernameNotFoundException
  │
  ├─ 2. Check user.isVerified == true
  │       └─ false → 403 EmailIsNotVerified
  │
  ├─ 3. passwordEncoder.matches(rawPassword, hashedPassword)
  │       └─ false → 401 UsernameNotFoundException ("Invalid credentials")
  │
  ├─ 4. JwtService.generateAccessToken(user)
  │       └─ Signs a JWT with: subject=email, userId, role, iat, exp (+30 min)
  │
  ├─ 5. RefreshTokenService.createRefreshToken(user)
  │       └─ Generates UUID → saves to refresh_tokens table (expires in 7 days)
  │
  └─ 6. Sets refresh token as HttpOnly cookie on the response
          └─ path=/api/v1/auth, maxAge=7 days, httpOnly=true
```

### Response
**Body:**
```json
{
  "token": "<JWT access token>",
  "message": "Login successfully",
  "status": 200,
  "timestamp": "2026-05-06T10:00:00"
}
```

**Cookie (automatic, not visible in body):**
```
Set-Cookie: refreshToken=<uuid>; Path=/api/v1/auth; HttpOnly; Max-Age=604800
```

---

## 2. Using the Access Token (Authenticated Requests)

After login, include the JWT in every protected request:

```
Authorization: Bearer <JWT access token>
```

### What JwtAuthFilter does on each request

```
Request with Authorization: Bearer <token>
  │
  ├─ 1. Read Authorization header
  ├─ 2. Extract token (remove "Bearer " prefix)
  ├─ 3. JwtService.extractEmail(token) → parse JWT → get subject (email)
  ├─ 4. UserDetailsService.loadUserByUsername(email) → hit DB
  ├─ 5. JwtService.isTokenValid(token, userDetails)
  │       ├─ email matches userDetails.username? ✅
  │       └─ token not expired? ✅
  ├─ 6. Build UsernamePasswordAuthenticationToken
  └─ 7. Set it in SecurityContextHolder → request is authenticated
```

---

## 3. Token Details

### Access Token (JWT)
| Field      | Value                          |
|------------|-------------------------------|
| Type       | JWT (signed HS256)            |
| Location   | Response body → use as Bearer |
| Expires    | 30 minutes                    |
| Contains   | email, userId, name, role     |

### Refresh Token
| Field      | Value                          |
|------------|-------------------------------|
| Type       | UUID (opaque)                  |
| Location   | HttpOnly Cookie                |
| Expires    | 7 days                         |
| Stored in  | `refresh_tokens` table in DB  |

---

## 4. Logout Flow

### Endpoint
```
POST /api/v1/auth/logout
Authorization: Bearer <access token>   ← required
```

### What happens

```
Client
  │
  │  POST /api/v1/auth/logout + Bearer token
  ▼
JwtAuthFilter
  │
  │  Validates Bearer token → sets authentication in SecurityContext
  ▼
SecurityConfig
  │
  │  /api/v1/auth/logout → .authenticated() → checks SecurityContext ✅
  ▼
AuthController.logout()
  │
  ▼
AuthServiceImpl.logout()
  │
  ├─ 1. Find refresh token in DB by cookie value
  │       └─ Found → set isRevoked = true → save
  │
  └─ 2. Clear the HttpOnly cookie (maxAge = 0)
```

---

## 5. Error Responses

| Scenario                  | Status | Message                  |
|---------------------------|--------|--------------------------|
| User not found            | 401    | User not found           |
| Email not verified        | 403    | Email is not verified    |
| Wrong password            | 401    | Invalid credentials      |
| Missing/invalid JWT       | 401    | (blocked by Spring Security) |
| Validation failed         | 400    | Field-specific message   |
