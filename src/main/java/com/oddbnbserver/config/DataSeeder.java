package com.oddbnbserver.config;

import com.oddbnbserver.models.*;
import com.oddbnbserver.repositories.ListingRepo;
import com.oddbnbserver.repositories.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;
import java.util.List;
import java.util.Random;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            UserRepo userRepo,
            ListingRepo listingRepo,
            PasswordEncoder encoder
    ) {
        return args -> {

            if (listingRepo.count() > 0) return;

            Random rand = new Random();

            // ==============================
            // 1) CREATE HOST USERS
            // ==============================
            for (int i = 1; i <= 5; i++) {
                User host = new User();
                host.setFirstName("Host" + i);
                host.setLastName("User");
                host.setEmail("host" + i + "@oddbnb.com");
                host.setPasswordHash(encoder.encode("password"));
                host.setRole(User.Role.HOST);

                userRepo.save(host);
            }

            List<User> hosts = userRepo.findAll();

            // ==============================
            // 2) CREATE LISTINGS
            // ==============================
            for (int i = 1; i <= 100; i++) {

                User host = hosts.get(rand.nextInt(hosts.size()));

                Listing listing = new Listing();

                listing.setTitle("Cozy Stay #" + i);
                listing.setDescription("Beautiful place to relax.");
                listing.setPricePerNight((double) (50 + rand.nextInt(300)));
                listing.setLocation(randomCity(rand));
                listing.setLat(randomLat(rand));
                listing.setLon(randomLon(rand));
                listing.setBeds(1 + rand.nextInt(5));
                listing.setBaths(1 + rand.nextInt(3));
                listing.setSquareFeet((double) (500 + rand.nextInt(2500)));
                listing.setCapacity(1 + rand.nextInt(8));
                listing.setAvailable(rand.nextBoolean());
                listing.setHost(host);

                // REQUIRED FIELDS
                listing.setCheckInTime(LocalTime.of(15, 0));
                listing.setCheckOutTime(LocalTime.of(11, 0));

                // ==============================
                // AMENITIES (ONE ONLY)
                // ==============================
                Amenities amenities = new Amenities();
                amenities.setListing(listing);

                amenities.setDesertView(rand.nextBoolean());
                amenities.setMountainView(rand.nextBoolean());
                amenities.setValleyView(rand.nextBoolean());
                amenities.setWifi(true);
                amenities.setTv(rand.nextBoolean());
                amenities.setKitchen(true);
                amenities.setWasher(rand.nextBoolean());
                amenities.setDryer(rand.nextBoolean());
                amenities.setPetsAllowed(rand.nextBoolean());
                amenities.setSmokeAlarm(true);

                listing.setAmenities(amenities);

                // ==============================
                // IMAGES
                // ==============================
                for (int img = 1; img <= 3; img++) {
                    ListingImage image = new ListingImage();
                    image.setListing(listing);
                    image.setImageUrl(
                            "https://picsum.photos/seed/"
                                    + i + "-" + img + "/800/600"
                    );

                    listing.getImages().add(image);
                }

                // ==============================
                // SAVE ONCE
                // ==============================
                listingRepo.save(listing);
            }

            System.out.println("🔥 Seeded 100 listings successfully!");
        };
    }

    // ==============================
    // RANDOM HELPERS
    // ==============================

    private String randomCity(Random rand) {
        List<String> cities = List.of(
                "Denver, CO",
                "Austin, TX",
                "Seattle, WA",
                "San Diego, CA",
                "Boise, ID",
                "Nashville, TN",
                "Phoenix, AZ",
                "Portland, OR"
        );
        return cities.get(rand.nextInt(cities.size()));
    }

    private double randomLat(Random rand) {
        return 25 + rand.nextDouble() * 20;
    }

    private double randomLon(Random rand) {
        return -125 + rand.nextDouble() * 30;
    }
}