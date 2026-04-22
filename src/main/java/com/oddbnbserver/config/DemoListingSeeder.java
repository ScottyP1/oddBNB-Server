package com.oddbnbserver.config;

import com.oddbnbserver.models.Amenities;
import com.oddbnbserver.models.Listing;
import com.oddbnbserver.models.ListingImage;
import com.oddbnbserver.models.User;
import com.oddbnbserver.repositories.ListingImageRepo;
import com.oddbnbserver.repositories.ListingRepo;
import com.oddbnbserver.repositories.UserRepo;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
public class DemoListingSeeder implements ApplicationRunner {

    private final UserRepo userRepo;
    private final ListingRepo listingRepo;
    private final ListingImageRepo listingImageRepo;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_EMAIL = "admin@oddbnb.com";
    private static final String ADMIN_PASSWORD = "password";
    private static final String HOST_EMAIL = "host@oddbnb.com";
    private static final String HOST_PASSWORD = "password";
    private static final String USER_EMAIL = "user@oddbnb.com";
    private static final String USER_PASSWORD = "password";

    public DemoListingSeeder(
            UserRepo userRepo,
            ListingRepo listingRepo,
            ListingImageRepo listingImageRepo,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepo = userRepo;
        this.listingRepo = listingRepo;
        this.listingImageRepo = listingImageRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepo.count() > 0 || listingRepo.count() > 0) {
            System.out.println("Demo listing seed skipped: users or listings table is not empty.");
            return;
        }

        SeedUsers users = ensureSeedUsers();
        int createdCount = 0;

        for (SeedListing seed : SEED_LISTINGS) {
            if (listingImageRepo.existsByImageUrl(seed.imageUrl())) {
                continue;
            }

            listingRepo.save(buildListing(users.host(), seed));
            createdCount++;
        }

        System.out.printf(
                "Demo listing seed complete: admin=%s, host=%s, user=%s, created=%d, skipped=%d%n",
                users.admin().getEmail(),
                users.host().getEmail(),
                users.user().getEmail(),
                createdCount,
                SEED_LISTINGS.size() - createdCount
        );
    }

    private SeedUsers ensureSeedUsers() {
        User admin = ensureSeedUser(ADMIN_EMAIL, "Admin", "Account", ADMIN_PASSWORD, User.Role.ADMIN);
        User host = ensureSeedUser(HOST_EMAIL, "Host", "Account", HOST_PASSWORD, User.Role.HOST);
        User user = ensureSeedUser(USER_EMAIL, "User", "Account", USER_PASSWORD, User.Role.GUEST);
        return new SeedUsers(admin, host, user);
    }

    private User ensureSeedUser(
            String email,
            String firstName,
            String lastName,
            String rawPassword,
            User.Role role
    ) {
        User user = userRepo.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setPasswordHash(passwordEncoder.encode(rawPassword));
            newUser.setRole(role);
            return userRepo.save(newUser);
        });

        boolean dirty = false;

        if (user.getRole() != role) {
            user.setRole(role);
            dirty = true;
        }

        if (!firstName.equals(user.getFirstName())) {
            user.setFirstName(firstName);
            dirty = true;
        }

        if (!lastName.equals(user.getLastName())) {
            user.setLastName(lastName);
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

    private record SeedUsers(User admin, User host, User user) {
    }

    private static final List<SeedListing> SEED_LISTINGS = List.of(
            new SeedListing(
                    "Alien Saucer Escape",
                    "/listings/alien.png",
                    "Built around the imported alien saucer image: a retro UFO stay with glowing windows, metallic curves, and full desert-crash-site energy.",
                    "Roswell, New Mexico",
                    35.1983,
                    -106.663,
                    289,
                    420,
                    1,
                    1,
                    2,
                    LocalTime.parse("16:00:00"),
                    LocalTime.parse("11:00:00"),
                    true,
                    List.of("desertView", "kitchen", "smokeAlarm", "tv", "wifi")
            ),
            new SeedListing(
                    "Barrel Cabin Retreat",
                    "/listings/barrel.png",
                    "Based on the barrel image you imported: a round cedar stay with a storybook door, warm wood walls, and compact cabin comfort.",
                    "Portland, Oregon",
                    45.5231,
                    -122.6765,
                    224,
                    310,
                    1,
                    1,
                    2,
                    LocalTime.parse("15:00:00"),
                    LocalTime.parse("10:00:00"),
                    true,
                    List.of("kitchen", "smokeAlarm", "valleyView", "wifi")
            ),
            new SeedListing(
                    "Cliffside Glass House",
                    "/listings/cliffSide.png",
                    "Mapped directly to the cliffSide image: a dramatic glass house on the edge of the water with huge views and a clean modern interior.",
                    "Laguna Beach, California",
                    33.8121,
                    -117.919,
                    615,
                    1850,
                    3,
                    2,
                    6,
                    LocalTime.parse("16:00:00"),
                    LocalTime.parse("11:00:00"),
                    true,
                    List.of("dryer", "kitchen", "smokeAlarm", "tv", "washer", "wifi")
            ),
            new SeedListing(
                    "Flintstone Stone House",
                    "/listings/flinstone.png",
                    "Inspired by the flinstone image: a rounded stone home with prehistoric-cartoon vibes, but still set up like a real getaway.",
                    "Tucson, Arizona",
                    32.2226,
                    -110.9747,
                    198,
                    540,
                    2,
                    1,
                    4,
                    LocalTime.parse("15:00:00"),
                    LocalTime.parse("10:00:00"),
                    true,
                    List.of("desertView", "kitchen", "mountainView", "petsAllowed", "smokeAlarm", "wifi")
            ),
            new SeedListing(
                    "Hillside Hobbit Hideaway",
                    "/listings/hillside.png",
                    "Pulled from the hillside image: a tucked-away earth home with grass on the roof, curved walls, and a quiet hidden feel.",
                    "Hocking Hills, Ohio",
                    39.113,
                    -82.536,
                    245,
                    460,
                    1,
                    1,
                    2,
                    LocalTime.parse("15:00:00"),
                    LocalTime.parse("10:00:00"),
                    true,
                    List.of("kitchen", "petsAllowed", "smokeAlarm", "valleyView", "wifi")
            ),
            new SeedListing(
                    "Hornet Nest Tree Pod",
                    "/listings/hornetNest.png",
                    "Modeled on the hornetNest image: a suspended woodland pod with a woven shell, wrapped in trees and built for a one-of-a-kind stay.",
                    "Forest of Dean, England",
                    51.5074,
                    -2.318,
                    267,
                    280,
                    1,
                    1,
                    2,
                    LocalTime.parse("16:00:00"),
                    LocalTime.parse("11:00:00"),
                    true,
                    List.of("mountainView", "smokeAlarm", "valleyView", "wifi")
            ),
            new SeedListing(
                    "Lake Cabin Lodge",
                    "/listings/lakeCabin.png",
                    "This one follows the lakeCabin image exactly: a timber lodge on bright blue water with mountain views and a private dock feel.",
                    "Lake Louise, Alberta",
                    51.4254,
                    -116.1773,
                    472,
                    1320,
                    2,
                    2,
                    5,
                    LocalTime.parse("16:00:00"),
                    LocalTime.parse("11:00:00"),
                    true,
                    List.of("dryer", "kitchen", "mountainView", "smokeAlarm", "tv", "valleyView", "washer", "wifi")
            ),
            new SeedListing(
                    "Snowglobe Bubble Stay",
                    "/listings/snowglobe.png",
                    "Taken from the snowglobe image: a transparent bubble stay in the woods where the whole point is stargazing from bed.",
                    "Normandy Woodland, France",
                    48.8566,
                    2.3522,
                    338,
                    390,
                    1,
                    1,
                    2,
                    LocalTime.parse("17:00:00"),
                    LocalTime.parse("10:00:00"),
                    true,
                    List.of("smokeAlarm", "valleyView", "wifi")
            ),
            new SeedListing(
                    "Desert Bell Tent Camp",
                    "/listings/tent.png",
                    "Based on the tent image you already imported: a desert bell tent setup with soft lighting, open sky, and simple glamping comfort.",
                    "Las Vegas Desert Rim, Nevada",
                    36.1699,
                    -115.1398,
                    176,
                    240,
                    1,
                    1,
                    2,
                    LocalTime.parse("15:00:00"),
                    LocalTime.parse("10:00:00"),
                    true,
                    List.of("desertView", "mountainView", "smokeAlarm", "wifi")
            ),
            new SeedListing(
                    "Underwater Suite",
                    "/listings/underwater.png",
                    "Built from the underwater image: a submerged bedroom surrounded by blue water, panoramic glass, and full aquarium-level atmosphere.",
                    "Rangali Island, Maldives",
                    4.1755,
                    73.5093,
                    1299,
                    710,
                    1,
                    1,
                    2,
                    LocalTime.parse("17:00:00"),
                    LocalTime.parse("11:00:00"),
                    true,
                    List.of("smokeAlarm", "tv", "wifi")
            )
    );
}
