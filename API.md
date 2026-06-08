# API Reference

Kettu exposes a JSON REST API. This document is generated from the controllers in
`src/main/java/com/khorunaliyev/kettu/controller`. For the authoritative, always
up-to-date schema (exact response payloads), use the live OpenAPI docs:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Conventions

### Base URL

`http://localhost:8080` by default.

### Response envelope

Most endpoints return a JSON object with this shape (Java record
`dto.reponse.Response`):

```json
{
  "message": "Success",
  "data": { }
}
```

`data` may be an object, an array, a string, `null`, or a validation-error map,
depending on the endpoint. (The `getImage` endpoint is the exception: it returns raw
image bytes with `Content-Type: image/jpeg`.)

### Authentication

Obtain a JWT through the Google OAuth2 flow, then send it on each request:

```
Authorization: Bearer <JWT>
```

Login flow: open `GET /api/auth/login` in a browser → Google consent → redirect to
`/auth-redirect.html?token=<JWT>`. See [ARCHITECTURE.md](ARCHITECTURE.md#oauth2-login).

### Authorization

Access is enforced by URL rules in `SecurityConfiguration`:

- `GET /api/places/**`, `/api/geo-data/**`, `/api/files/**`, `/api/auth/**`,
  `GET /api/resources/**`, Swagger and OAuth2 paths are **public**.
- `POST`/`PUT`/`PATCH` on `/api/resources/**` require the **`ADMIN`** authority.
- `/api/secure/**` requires **`ADMIN`**.
- Everything else requires an **authenticated** user.

### Error responses

Errors are returned via the global exception handler with consistent statuses:

| Status | When                                                        |
|--------|-------------------------------------------------------------|
| 400    | Validation failure, missing parameter/header, malformed JSON |
| 401    | Invalid credentials                                         |
| 403    | Authenticated but not authorized                            |
| 404    | Resource / endpoint not found                              |
| 405    | HTTP method not supported                                  |
| 409    | Data integrity / storage conflict                          |
| 415    | Unsupported media type                                     |
| 422    | Referenced entity not found                                |
| 500    | Unexpected server error (generic message; no internals leaked) |

Example validation error (400):

```json
{
  "message": "Validation error",
  "data": { "name": "size must be between 1 and 50" }
}
```

---

## Auth — `/api/auth`

| Method | Path                       | Auth   | Description                                  |
|--------|----------------------------|--------|----------------------------------------------|
| ANY    | `/api/auth/login`          | Public | Redirects to Google OAuth2 authorization.    |
| ANY    | `/api/auth/login/redirect` | Public | Returns a plain confirmation string.         |

---

## Places — `/api/places`

| Method | Path                          | Auth          | Description                          |
|--------|-------------------------------|---------------|--------------------------------------|
| GET    | `/api/places`                 | Public        | List/filter places.                  |
| GET    | `/api/places/uploading`       | Authenticated | Check the caller's in-progress uploads. |
| POST   | `/api/places/save`            | Authenticated | Create a place (multipart).          |
| PATCH  | `/api/places/{id}/update`     | Authenticated | Update a place.                      |
| POST   | `/api/places/{id}/update-status` | Authenticated | Change a place's moderation status. |

**`GET /api/places`** query parameters (all optional): `status` (`PlaceStatus`
enum), `name`, `category_id`, `tag_ids` (repeatable), `region_id`, `district_id`.

```bash
curl "http://localhost:8080/api/places?name=cafe&category_id=3"
```

**`POST /api/places/save`** — `multipart/form-data` with parts:

- `data` (JSON, validated `PlaceRequest`): `name` (1–50), `description` (20–500),
  `category_id` (positive), `tags` (1–10 integer ids), `place_location`
  (`{ "lat_": <positive double>, "long_": <positive double> }`).
- `main_photo` (file, required).
- `additional_photos` (files, optional).

**`PATCH /api/places/{id}/update`** — JSON `PlaceUpdateRequest`: `name` (1–50),
`description` (1–500), `placePhotos` (non-empty list of
`{ "imageName": string, "isMain": boolean }`), optional `placeLocation`,
`placeMetaData`, `nearbyThings` (list of ids).

**`POST /api/places/{id}/update-status`** — JSON `PlaceUpdateStatusRequest`:
`status` (required string), `changeReason` (optional, 1–500).

---

## Geo — `/api/geo-data`

| Method | Path            | Auth   | Description                                   |
|--------|-----------------|--------|-----------------------------------------------|
| GET    | `/api/geo-data` | Public | Resolve a lat/long point to region + district. |

```bash
curl "http://localhost:8080/api/geo-data?lat=41.31&long=69.24"
```

```json
{
  "message": "Success",
  "data": {
    "region": { "id": 1, "name": "Tashkent" },
    "district": { "id": 7, "name": "Chilonzor" }
  }
}
```

Returns 404 if no district contains the point.

---

## Resources — `/api/resources`

Writes (`POST`/`PUT`/`PATCH`) require `ADMIN`. Reads (`GET`) are public.

### Category — `/api/resources/category`

| Method | Path                                   | Auth   | Body / params                                  |
|--------|----------------------------------------|--------|------------------------------------------------|
| GET    | `/api/resources/category/`             | Public | —                                              |
| GET    | `/api/resources/category/{id}`         | Public | —                                              |
| POST   | `/api/resources/category/save`         | ADMIN  | `CategoryNameRequest`: `name` (1–100), `slug` (1–100) |
| POST   | `/api/resources/category/import`       | ADMIN  | `file` (multipart Excel)                        |
| PUT    | `/api/resources/category/{id}/update`  | ADMIN  | `CategoryNameRequest`                           |
| POST   | `/api/resources/category/assign-tags`  | ADMIN  | `CategoryTagsRequest`: `category_id`, `tag_ids` |
| DELETE | `/api/resources/category/unassign-tags`| (see note) | `CategoryTagsRequest`                       |

> Note: `DELETE` is not covered by the `ADMIN` `POST/PUT/PATCH` matchers and falls
> under the broad `/api/resources/**` rule in `SecurityConfiguration`. Confirm the
> intended access for `unassign-tags` before relying on it.

### Country — `/api/resources/country`

| Method | Path                                  | Auth   | Body / params                          |
|--------|---------------------------------------|--------|----------------------------------------|
| GET    | `/api/resources/country`              | Public | —                                      |
| POST   | `/api/resources/country/save`         | ADMIN  | `CountryNameRequest`: `name` (4–56)    |
| PATCH  | `/api/resources/country/{id}/update`  | ADMIN  | `CountryNameRequest`                    |
| POST   | `/api/resources/country/import`       | ADMIN  | `file` (multipart Excel)               |

### Region — `/api/resources/region`

| Method | Path                                 | Auth   | Body / params                                       |
|--------|--------------------------------------|--------|-----------------------------------------------------|
| GET    | `/api/resources/region?country={id}` | Public | `country` (query, required)                          |
| POST   | `/api/resources/region/save`         | ADMIN  | `NewRegionRequest`: `name` (1–85), `countryId`       |
| PATCH  | `/api/resources/region/{id}/update`  | ADMIN  | `RegionNameRequest`: `name` (1–85)                   |
| POST   | `/api/resources/region/import`       | ADMIN  | `country_id` (param), `file` (multipart Excel)       |

### District — `/api/resources/districts`

| Method | Path                                    | Auth   | Body / params                                       |
|--------|-----------------------------------------|--------|-----------------------------------------------------|
| GET    | `/api/resources/districts/?region={id}` | Public | `region` (query, required)                          |
| GET    | `/api/resources/districts/geo-data`     | Public | `latitude`, `longitude` (query)                     |
| POST   | `/api/resources/districts/save`         | ADMIN  | `NewDistrictRequest`: `name` (1–100), `regionId`    |
| PATCH  | `/api/resources/districts/{id}/update`  | ADMIN  | `DistrictNameRequest`: `name` (1–100)               |
| POST   | `/api/resources/districts/import`       | ADMIN  | `region_id` (param), `file` (multipart GeoJSON)     |

### Tag — `/api/resources/tag`

| Method | Path                              | Auth   | Body / params              |
|--------|-----------------------------------|--------|----------------------------|
| GET    | `/api/resources/tag/`             | Public | —                          |
| POST   | `/api/resources/tag/create`       | ADMIN  | `name` (query param)       |
| PUT    | `/api/resources/tag/{id}/update`  | ADMIN  | `name` (query param)       |

---

## Files — `/api/files`

| Method | Path                          | Auth   | Description                                   |
|--------|-------------------------------|--------|-----------------------------------------------|
| POST   | `/api/files/upload-single`    | Public | Upload one image (`file`); returns its key.   |
| POST   | `/api/files/upload-multiple`  | Public | Upload many images (`files`); non-images are skipped. |
| GET    | `/api/files/{key}`            | Public | Get the public URL for a stored object.       |
| GET    | `/api/files/image/{filename}` | Public | Stream raw image bytes (`image/jpeg`).        |

```bash
curl -F "file=@photo.jpg" http://localhost:8080/api/files/upload-single
```

---

## User — `/api/user`

| Method | Path                  | Auth          | Body / params                                   |
|--------|-----------------------|---------------|-------------------------------------------------|
| GET    | `/api/user/me`        | Authenticated | —                                               |
| PATCH  | `/api/user/update`    | Authenticated | multipart: `data` (`UserUpdate`: `name` 1–255, `bio` 1–500), `profile_photo`, `background_photo` |
| POST   | `/api/user/first-login` | Authenticated | —                                             |

---

## Secure — `/secure`

| Method | Path          | Auth                 | Description                          |
|--------|---------------|----------------------|--------------------------------------|
| GET    | `/secure/me`  | Authenticated, `ADMIN` (`@PreAuthorize`) | Returns the admin user record. |

---

## Static / web

| Method | Path                  | Auth   | Description                              |
|--------|-----------------------|--------|------------------------------------------|
| GET    | `/`                   | Public | Forwards to `index.html`.                |
| GET    | `/auth-redirect.html` | Public | OAuth2 redirect landing page with token. |
