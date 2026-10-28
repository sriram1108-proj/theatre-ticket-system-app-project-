# Theatre Ticket Management System (Smart Campus) - Backend

Spring Boot 3 + Spring Data JPA + H2 (in-memory). Java 17+.

## Run

```bash
mvn spring-boot:run
```

API: http://localhost:8080  |  H2 console: http://localhost:8080/h2-console
(JDBC URL `jdbc:h2:mem:theatredb`, user `sa`, empty password)

Sample shows and two users are loaded on startup (see `config/DataSeeder.java`).

## Endpoints

| Method | URL | Purpose |
|---|---|---|
| POST | /api/shows | Create a show |
| GET | /api/shows?upcoming=true&search=ham | List / search shows |
| GET | /api/shows/{id} | Show details + available seat count |
| PUT | /api/shows/{id} | Update a show |
| DELETE | /api/shows/{id} | Delete (only if it has no bookings) |
| GET | /api/shows/{id}/seats | Seat map: booked and available seats |
| GET | /api/shows/{id}/bookings | All bookings for a show (admin view) |
| POST | /api/users | Register a campus user |
| GET | /api/users, /api/users/{id} | List / get users |
| GET | /api/users/{id}/bookings | A user's booking history |
| POST | /api/bookings | Book seats |
| GET | /api/bookings/{id} | Get booking |
| GET | /api/bookings/code/{code} | Look up by ticket code (e.g. TKT-1A2B3C4D) |
| DELETE | /api/bookings/{id} | Cancel booking and free the seats |

## Try it

```bash
# seat map for show 1
curl localhost:8080/api/shows/1/seats

# book seats A1, A2 as user 1 for show 1
curl -X POST localhost:8080/api/bookings -H "Content-Type: application/json" \
  -d '{"userId":1,"showId":1,"seats":["A1","A2"]}'

# same seats again -> 409 Conflict
# cancel booking 1
curl -X DELETE localhost:8080/api/bookings/1

# create a show
curl -X POST localhost:8080/api/shows -H "Content-Type: application/json" \
  -d '{"title":"Othello","description":"Drama","genre":"Drama","durationMinutes":140,
       "showTime":"2026-12-05T18:30:00","hall":"Main Auditorium","ticketPrice":150,
       "totalRows":8,"seatsPerRow":10}'
```

## Business rules

- Seats are labelled by row letter + number (A1, B12). Layout = `totalRows` x `seatsPerRow`.
- No double booking: a unique DB constraint on (show, seat) backs up the service-level check.
- Max 6 seats per booking and 2-hour cancellation cutoff (change in `application.properties`).
- Cancelling deletes the seat rows, so seats immediately become available again.
- Errors return JSON with 400 (bad input), 404 (not found) or 409 (conflict).

## Project layout

```
model/       JPA entities: Show, AppUser, Booking, BookedSeat
repository/  Spring Data repositories
dto/         Request/response records with validation
service/     Business logic (BookingService has the booking/cancel rules)
controller/  REST controllers
exception/   Custom exceptions + GlobalExceptionHandler
config/      DataSeeder (sample data)
```

## Ideas for extending it

- Switch to MySQL: replace the `h2` dependency with `mysql-connector-j` and update the datasource properties.
- Add Spring Security + JWT with ADMIN / STUDENT roles.
- Payments, QR code on tickets, email confirmation, seat-hold timeouts.
