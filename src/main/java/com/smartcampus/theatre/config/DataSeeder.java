package com.smartcampus.theatre.config;

import com.smartcampus.theatre.model.AppUser;
import com.smartcampus.theatre.model.Show;
import com.smartcampus.theatre.repository.ShowRepository;
import com.smartcampus.theatre.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Loads sample data on startup so the API can be tried immediately (H2 in-memory DB starts empty). */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(ShowRepository shows, UserRepository users) {
        return args -> {
            if (shows.count() > 0) return;

            shows.save(show("Hamlet", "Drama club's take on the Shakespeare classic.", "Drama", 150,
                    LocalDateTime.now().plusDays(5).withHour(18).withMinute(30).withSecond(0).withNano(0),
                    "Main Auditorium", "150.00", 8, 10));
            shows.save(show("Campus Comedy Night", "Stand-up by student comedians.", "Comedy", 90,
                    LocalDateTime.now().plusDays(9).withHour(19).withMinute(0).withSecond(0).withNano(0),
                    "Mini Theatre", "100.00", 5, 8));
            shows.save(show("Annual Day Musical", "Music and dance showcase.", "Musical", 120,
                    LocalDateTime.now().plusDays(20).withHour(17).withMinute(0).withSecond(0).withNano(0),
                    "Main Auditorium", "200.00", 10, 12));

            users.save(user("Asha Verma", "asha@campus.edu", "CS21B001"));
            users.save(user("Ravi Kumar", "ravi@campus.edu", "EC21B014"));
        };
    }

    private Show show(String title, String desc, String genre, int mins, LocalDateTime time,
                      String hall, String price, int rows, int perRow) {
        Show s = new Show();
        s.setTitle(title);
        s.setDescription(desc);
        s.setGenre(genre);
        s.setDurationMinutes(mins);
        s.setShowTime(time);
        s.setHall(hall);
        s.setTicketPrice(new BigDecimal(price));
        s.setTotalRows(rows);
        s.setSeatsPerRow(perRow);
        return s;
    }

    private AppUser user(String name, String email, String campusId) {
        AppUser u = new AppUser();
        u.setName(name);
        u.setEmail(email);
        u.setCampusId(campusId);
        return u;
    }
}
