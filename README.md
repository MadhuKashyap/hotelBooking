<<<<<<< HEAD
# Hotel Booking API

A Spring Boot RESTful API for hotel room booking, user management, and booking history. Easily deployable with Docker.

---

## 🚀 Features
- User registration and login
- Hotel and room management
- Book and cancel rooms
- View booking history
- Health check endpoint
- Dockerized for easy deployment

---

## 📦 API Endpoints

### User
- `POST /hotel-app/users/signup` — Register a new user
- `POST /hotel-app/users/login` — User login
- `POST /hotel-app/users/add` — Add user
- `PUT /hotel-app/users/update` — Update user

### Hotel
- `POST /hotel-app/hotels/fetch-all` — Fetch hotels (with filters)
- `GET /hotel-app/hotels/fetch-rooms?hotelId=1` — Fetch rooms by hotel
- `POST /hotel-app/hotels/book-room` — Book a room
- `GET /hotel-app/hotels/cancel-room?bookingId=1` — Cancel a booking
- `POST /hotel-app/hotels/view-bookings` — View booking history

### Health
- `GET /hotel-app/health` — Health check

---

## 🛠️ Getting Started

### Prerequisites
- Java 17+
- Maven
- MySQL (or update datasource in `application.properties`)

### Build & Run
```sh
mvn clean package
java -jar target/hotelBooking-0.0.1-SNAPSHOT.jar
```

App will be available at: [http://localhost:8080/hotel-app](http://localhost:8080/hotel-app)

---

## 🐳 Docker

### Build Docker Image
```sh
docker build -t hotel-booking-app .
```

### Run Docker Container
```sh
docker run -p 8080:8080 hotel-booking-app
```

---

## 📚 Documentation
- API endpoints are RESTful and can be tested with Postman or curl.
- (Optional) Add Swagger UI for interactive docs.

---

## 🤝 Contributing
Pull requests are welcome! For major changes, please open an issue first.


## 📬 Contact
For questions, contact the maintainer at [cv.madhu01@gmail.com]. 

