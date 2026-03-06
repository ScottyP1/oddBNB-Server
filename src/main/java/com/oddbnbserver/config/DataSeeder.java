package com.oddbnbserver.config;

import com.oddbnbserver.models.*;
import com.oddbnbserver.repositories.ListingRepo;
import com.oddbnbserver.repositories.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;
import java.util.*;

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

            Optional<User> existingAdmin = userRepo.findByEmail("admin@oddbnb.com");

            if (existingAdmin.isEmpty()) {

                User admin = new User();

                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setEmail("admin@oddbnb.com");
                admin.setPasswordHash(encoder.encode("password"));
                admin.setRole(User.Role.ADMIN);

                userRepo.save(admin);
            }
            // ==============================
            // CREATE HOSTS
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
            // LISTING TEMPLATES
            // ==============================

            List<ListingTemplate> templates = List.of(
                    new ListingTemplate(
                            "Modern Desert Retreat",
                            "Scottsdale, AZ",
                            33.4942, -111.9261,
                            "Luxury desert home with floor-to-ceiling windows overlooking the Sonoran landscape.",
                            List.of(
                                    "https://images.unsplash.com/photo-1600585154340-be6161a56a0c",
                                    "https://images.unsplash.com/photo-1600607687920-4e2a09cf159d",
                                    "https://images.unsplash.com/photo-1560185127-6ed189bf02f4"
                            )
                    ),

                    new ListingTemplate(
                            "Mountain Cabin Escape",
                            "Aspen, CO",
                            39.1911, -106.8175,
                            "Cozy cabin tucked into the Rocky Mountains with wood burning fireplace and forest views.",
                            List.of(
                                    "https://images.unsplash.com/photo-1518780664697-55e3ad937233",
                                    "https://images.unsplash.com/photo-1601918774946-25832a4be0d6",
                                    "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85"
                            )
                    ),

                    new ListingTemplate(
                            "Oceanfront Glass House",
                            "Malibu, CA",
                            34.0259, -118.7798,
                            "Minimalist beachfront home with panoramic ocean views and private deck.",
                            List.of(
                                    "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85",
                                    "https://images.unsplash.com/photo-1600585154526-990dced4db0d",
                                    "https://images.unsplash.com/photo-1600047509782-20d39509f26d"
                            )
                    ),

                    new ListingTemplate(
                            "Urban Loft in Downtown",
                            "Chicago, IL",
                            41.8781, -87.6298,
                            "Industrial loft with exposed brick, skyline views, and walking distance to restaurants.",
                            List.of(
                                    "https://images.unsplash.com/photo-1493809842364-78817add7ffb",
                                    "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688",
                                    "https://images.unsplash.com/photo-1560448075-bb4caa6b1279"
                            )
                    ),

                    new ListingTemplate(
                            "Forest Treehouse Retreat",
                            "Portland, OR",
                            45.5152, -122.6784,
                            "Hand-built treehouse surrounded by towering pines with peaceful forest views.",
                            List.of(
                                    "https://images.unsplash.com/photo-1505691938895-1758d7feb511",
                                    "https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3",
                                    "https://images.unsplash.com/photo-1505692794403-34d4982e38fa"
                            )
                    )
            );

            // ==============================
            // GENERATE LISTINGS
            // ==============================

            for (int i = 0; i < 100; i++) {

                ListingTemplate template = templates.get(rand.nextInt(templates.size()));
                User host = hosts.get(rand.nextInt(hosts.size()));

                Listing listing = new Listing();

                listing.setTitle(template.title);
                listing.setDescription(template.description);
                listing.setLocation(template.location);
                listing.setLat(template.lat);
                listing.setLon(template.lon);

                listing.setPricePerNight((double) (120 + rand.nextInt(400)));
                listing.setBeds(1 + rand.nextInt(4));
                listing.setBaths(1 + rand.nextInt(3));
                listing.setSquareFeet(600.0 + rand.nextInt(2500));
                listing.setCapacity(2 + rand.nextInt(6));
                listing.setAvailable(true);

                listing.setHost(host);

                listing.setCheckInTime(LocalTime.of(15, 0));
                listing.setCheckOutTime(LocalTime.of(11, 0));

                // ==============================
                // AMENITIES
                // ==============================

                Amenities amenities = new Amenities();

                amenities.setListing(listing);

                amenities.setWifi(true);
                amenities.setKitchen(true);
                amenities.setTv(rand.nextBoolean());
                amenities.setWasher(rand.nextBoolean());
                amenities.setDryer(rand.nextBoolean());
                amenities.setPetsAllowed(rand.nextBoolean());

                amenities.setMountainView(rand.nextBoolean());
                amenities.setValleyView(rand.nextBoolean());
                amenities.setDesertView(rand.nextBoolean());

                amenities.setSmokeAlarm(true);

                listing.setAmenities(amenities);

                // ==============================
                // IMAGES
                // ==============================

                for (String url : template.images) {

                    ListingImage img = new ListingImage();

                    img.setListing(listing);
                    img.setImageUrl(url);

                    listing.getImages().add(img);
                }

                listingRepo.save(listing);
            }

            System.out.println("🔥 Seeded 100 realistic listings!");
        };
    }

    // ==============================
    // TEMPLATE CLASS
    // ==============================

    static class ListingTemplate {

        String title;
        String location;
        double lat;
        double lon;
        String description;
        List<String> images;

        ListingTemplate(
                String title,
                String location,
                double lat,
                double lon,
                String description,
                List<String> images
        ) {
            this.title = title;
            this.location = location;
            this.lat = lat;
            this.lon = lon;
            this.description = description;
            this.images = images;
        }
    }
}