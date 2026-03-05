# OddBNB Backend

OddBNB is a full-stack rental platform backend inspired by Airbnb, built with **Spring Boot**.
It provides REST APIs for managing users, listings, images, and authentication.

The backend handles:

* User authentication and authorization
* Listing creation and management
* Image uploads using AWS S3 presigned URLs
* Database persistence with MySQL
* Secure API access with JWT
* Cloud deployment using Docker and AWS ECS

---

# Tech Stack

**Backend Framework**

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security

**Database**

* MySQL

**Storage**

* AWS S3 (image uploads)

**Authentication**

* JWT (JSON Web Tokens)

**Infrastructure**

* Docker
* AWS ECS
* AWS Application Load Balancer

---

# Features

### Authentication

* Register new users
* Login with JWT authentication
* Role-based access (`ADMIN`, `HOST`, `GUEST`)

### Listings

Users can:

* Create listings
* View listings
* Update listings
* Delete listings

Each listing may contain multiple images stored in **AWS S3**.

### Image Uploads

Images are uploaded securely using **S3 presigned URLs**.

Flow:

1. Frontend requests presigned upload URL
2. Backend generates secure S3 upload link
3. Frontend uploads image directly to S3
4. Backend stores resulting image URL with listing

This approach prevents the backend from handling large file uploads.

### Users

Users can:

* Register
* Update profile
* Create listings
* Upload listing images

# Running Locally

### 1. Clone the repository

```
git clone https://github.com/yourusername/oddbnb-backend.git
cd oddbnb-backend
```
