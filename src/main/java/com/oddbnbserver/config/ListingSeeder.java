package com.oddbnbserver.config;

import com.oddbnbserver.models.Amenities;
import com.oddbnbserver.models.Listing;
import com.oddbnbserver.models.ListingImage;
import com.oddbnbserver.models.User;
import com.oddbnbserver.repositories.ListingRepo;
import com.oddbnbserver.repositories.UserRepo;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
public class ListingSeeder implements ApplicationRunner {

    private static final String SEED_PASSWORD = "password";
    private static final String ADMIN_EMAIL = "admin@email.com";
    private static final String USER_EMAIL = "user@email.com";
    private static final String HOST_EMAIL = "host@email.com";

    private final UserRepo userRepo;
    private final ListingRepo listingRepo;
    private final PasswordEncoder passwordEncoder;

    public ListingSeeder(
            UserRepo userRepo,
            ListingRepo listingRepo,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepo = userRepo;
        this.listingRepo = listingRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        SeedUsers users = ensureSeedUsers();

        if (listingRepo.count() > 0) {
            System.out.printf(
                    "Listing seed skipped: listings table already has data. admin=%s host=%s user=%s%n",
                    users.admin().getEmail(),
                    users.host().getEmail(),
                    users.user().getEmail()
            );
            return;
        }

        for (SeedListing seed : SEED_LISTINGS) {
            listingRepo.save(buildListing(users.host(), seed));
        }

        System.out.printf(
                "Listing seed complete: admin=%s, host=%s, user=%s, listings=%d%n",
                users.admin().getEmail(),
                users.host().getEmail(),
                users.user().getEmail(),
                SEED_LISTINGS.size()
        );
    }

    private SeedUsers ensureSeedUsers() {
        User admin = ensureSeedUser(ADMIN_EMAIL, "Admin", "User", User.Role.ADMIN);
        User host = ensureSeedUser(HOST_EMAIL, "Host", "User", User.Role.HOST);
        User user = ensureSeedUser(USER_EMAIL, "Guest", "User", User.Role.GUEST);
        return new SeedUsers(admin, host, user);
    }

    private User ensureSeedUser(String email, String firstName, String lastName, User.Role role) {
        User user = userRepo.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setPasswordHash(passwordEncoder.encode(SEED_PASSWORD));
            newUser.setRole(role);
            return userRepo.save(newUser);
        });

        boolean dirty = false;

        if (!firstName.equals(user.getFirstName())) {
            user.setFirstName(firstName);
            dirty = true;
        }

        if (!lastName.equals(user.getLastName())) {
            user.setLastName(lastName);
            dirty = true;
        }

        if (user.getRole() != role) {
            user.setRole(role);
            dirty = true;
        }

        if (dirty) {
            user = userRepo.save(user);
        }

        return user;
    }

    private Listing buildListing(User host, SeedListing seed) {
        Listing listing = new Listing();
        listing.setTitle(seed.title());
        listing.setDescription(seed.description());
        listing.setLocation(seed.location());
        listing.setLat(seed.lat());
        listing.setLon(seed.lon());
        listing.setPricePerNight(seed.pricePerNight());
        listing.setSquareFeet(seed.squareFeet());
        listing.setBeds(seed.beds());
        listing.setBaths(seed.baths());
        listing.setCapacity(seed.capacity());
        listing.setCheckInTime(seed.checkInTime());
        listing.setCheckOutTime(seed.checkOutTime());
        listing.setAvailable(seed.available());
        listing.setHost(host);

        Amenities amenities = new Amenities();
        amenities.setListing(listing);
        amenities.setDesertView(seed.amenities().contains("desertView"));
        amenities.setDryer(seed.amenities().contains("dryer"));
        amenities.setKitchen(seed.amenities().contains("kitchen"));
        amenities.setMountainView(seed.amenities().contains("mountainView"));
        amenities.setPetsAllowed(seed.amenities().contains("petsAllowed"));
        amenities.setSmokeAlarm(seed.amenities().contains("smokeAlarm"));
        amenities.setTv(seed.amenities().contains("tv"));
        amenities.setValleyView(seed.amenities().contains("valleyView"));
        amenities.setWasher(seed.amenities().contains("washer"));
        amenities.setWifi(seed.amenities().contains("wifi"));
        listing.setAmenities(amenities);

        ListingImage image = new ListingImage();
        image.setImageUrl(seed.imageUrl());
        image.setListing(listing);
        listing.getImages().add(image);

        return listing;
    }

    private record SeedUsers(User admin, User host, User user) {
    }

    private record SeedListing(
            String title,
            String imageUrl,
            String description,
            String location,
            double lat,
            double lon,
            double pricePerNight,
            double squareFeet,
            int beds,
            int baths,
            int capacity,
            LocalTime checkInTime,
            LocalTime checkOutTime,
            boolean available,
            List<String> amenities
    ) {
    }

    private static final List<SeedListing> SEED_LISTINGS = List.of(
            new SeedListing(
                    "Flying Saucer Desert Landing",
                    "https://oddbnb-images-bucket.s3.us-east-1.amazonaws.com/listings/alien.png",
                    "A retro flying saucer stay dropped into the desert, with metallic curves, glowing windows, and full alien-landing energy.",
                    "Roswell, New Mexico",
                    33.3943,
                    -104.5230,
                    285,
                    420,
                    1,
                    1,
                    2,
                    LocalTime.of(16, 0),
                    LocalTime.of(11, 0),
                    true,
                    List.of("desertView", "kitchen", "smokeAlarm", "tv", "wifi")
            ),
            new SeedListing(
                    "Storybook Barrel Cabin",
                    "https://oddbnb-images-bucket.s3.us-east-1.amazonaws.com/listings/barrel.png",
                    "A round barrel-shaped cabin with warm wood walls, a tiny storybook entrance, and a compact layout that feels cozy instead of cramped.",
                    "Portland, Oregon",
                    45.5152,
                    -122.6784,
                    224,
                    310,
                    1,
                    1,
                    2,
                    LocalTime.of(15, 0),
                    LocalTime.of(10, 0),
                    true,
                    List.of("kitchen", "smokeAlarm", "tv", "valleyView", "wifi")
            ),
            new SeedListing(
                    "Hornet Nest Floating House",
                    "https://oddbnb-images-bucket.s3.us-east-1.amazonaws.com/listings/hornetNest.png",
                    "A floating house with a woven hornet-nest shape, suspended over the water and built to feel strange, quiet, and unforgettable.",
                    "Lake Lanier, Georgia",
                    34.2806,
                    -83.8152,
                    348,
                    520,
                    1,
                    1,
                    2,
                    LocalTime.of(16, 0),
                    LocalTime.of(11, 0),
                    true,
                    List.of("kitchen", "smokeAlarm", "tv", "valleyView", "wifi")
            ),
            new SeedListing(
                    "Hillside Hidden House",
                    "https://oddbnb-images-bucket.s3.us-east-1.amazonaws.com/listings/hillside.png",
                    "A home built directly into the hillside, with a grass-covered roof, curved front entry, and a tucked-away storybook feel.",
                    "Hocking Hills, Ohio",
                    39.4362,
                    -82.5370,
                    245,
                    460,
                    1,
                    1,
                    2,
                    LocalTime.of(15, 0),
                    LocalTime.of(10, 0),
                    true,
                    List.of("kitchen", "mountainView", "petsAllowed", "smokeAlarm", "wifi")
            ),
            new SeedListing(
                    "Snowglobe Forest Stay",
                    "https://oddbnb-images-bucket.s3.us-east-1.amazonaws.com/listings/snowglobe.png",
                    "A transparent snowglobe-style bubble in the woods, designed for stargazing at night and waking up surrounded by trees.",
                    "Woodstock, Vermont",
                    43.6242,
                    -72.5187,
                    338,
                    390,
                    1,
                    1,
                    2,
                    LocalTime.of(17, 0),
                    LocalTime.of(10, 0),
                    true,
                    List.of("smokeAlarm", "valleyView", "wifi")
            ),
            new SeedListing(
                    "Flintstone Stone House",
                    "https://oddbnb-images-bucket.s3.us-east-1.amazonaws.com/listings/flinstone.png",
                    "A rounded stone house with prehistoric-cartoon energy, thick rock walls, and a playful shape that still works as a real getaway.",
                    "Tucson, Arizona",
                    32.2226,
                    -110.9747,
                    198,
                    540,
                    2,
                    1,
                    4,
                    LocalTime.of(15, 0),
                    LocalTime.of(10, 0),
                    true,
                    List.of("desertView", "kitchen", "mountainView", "petsAllowed", "smokeAlarm", "wifi")
            ),
            new SeedListing(
                    "Underwater Blue Reef Suite",
                    "https://oddbnb-images-bucket.s3.us-east-1.amazonaws.com/listings/underwater.png",
                    "An underwater suite wrapped in glass, surrounded by blue water and sea life, with a bedroom that feels like an aquarium observatory.",
                    "Rangali Island, Maldives",
                    3.2028,
                    73.1156,
                    1299,
                    710,
                    1,
                    1,
                    2,
                    LocalTime.of(17, 0),
                    LocalTime.of(11, 0),
                    true,
                    List.of("smokeAlarm", "tv", "wifi")
            )
    );
}
