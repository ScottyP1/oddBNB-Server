package com.oddbnbserver.config;

import com.oddbnbserver.models.Amenities;
import com.oddbnbserver.models.Listing;
import com.oddbnbserver.models.ListingImage;
import com.oddbnbserver.models.User;
import com.oddbnbserver.repositories.ListingImageRepo;
import com.oddbnbserver.repositories.ListingRepo;
import com.oddbnbserver.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.seed.demo-listings:false}")
    private boolean seedEnabled;

    @Value("${app.seed.demo-user-email:test@email.com}")
    private String seedEmail;

    @Value("${app.seed.demo-user-password:test1234}")
    private String seedPassword;

    @Value("${app.seed.demo-user-first-name:Test}")
    private String seedFirstName;

    @Value("${app.seed.demo-user-last-name:Host}")
    private String seedLastName;

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
        if (!seedEnabled) {
            return;
        }

        User host = ensureSeedHost();
        int createdCount = 0;

        for (SeedListing seed : SEED_LISTINGS) {
            if (listingImageRepo.existsByImageUrl(seed.imageUrl())) {
                continue;
            }

            listingRepo.save(buildListing(host, seed));
            createdCount++;
        }

        System.out.printf(
                "Demo listing seed complete: host=%s, created=%d, skipped=%d%n",
                host.getEmail(),
                createdCount,
                SEED_LISTINGS.size() - createdCount
        );
    }

    private User ensureSeedHost() {
        User user = userRepo.findByEmail(seedEmail).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(seedEmail);
            newUser.setFirstName(seedFirstName);
            newUser.setLastName(seedLastName);
            newUser.setPasswordHash(passwordEncoder.encode(seedPassword));
            newUser.setRole(User.Role.HOST);
            return userRepo.save(newUser);
        });

        if (user.getRole() == User.Role.GUEST) {
            user.setRole(User.Role.HOST);
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

    private static final List<SeedListing> SEED_LISTINGS = List.of(
            new SeedListing(
                    "Nebula Knockout Saucer",
                    "/listings/alien.png",
                    "Retro-futurist flying saucer stay with neon trim, glowing portholes, and midnight-abduction energy in the best possible way.",
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
                    "Cask Of Wonder Nook",
                    "/listings/barrel.png",
                    "A cedar barrel hideout with a storybook round door, warm wood interior, and just enough whimsy to make normal cabins jealous.",
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
                    "Tidal Velvet Cliffhouse",
                    "/listings/cliffSide.png",
                    "Glassy cliffside lounge with fireplace, wall-to-wall ocean views, and sunset bragging rights from every seat in the room.",
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
                    "Jurassic Moonstone Burrow",
                    "/listings/flinstone.png",
                    "A handmade stone-age fantasy dome glowing under the stars, perfect for guests who want Flintstones vibes without cave-person plumbing.",
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
                    "Moss Boss Shire Bunker",
                    "/listings/hillside.png",
                    "Half-hidden hillside home with mossy curves, tiny round windows, and strong secret-council-of-hobbits energy.",
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
                    "The Suspended Acorn Reactor",
                    "/listings/hornetNest.png",
                    "Suspended woodland orb for people who looked at a hornets nest and thought, yes, but make it boutique and adorable.",
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
                    "Glacier Gospel Lake Lodge",
                    "/listings/lakeCabin.png",
                    "Turquoise lakefront escape with cathedral peaks, dockside calm, and enough mountain drama to ruin ordinary weekends forever.",
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
                    "Starlight Snowglobe Hideout",
                    "/listings/snowglobe.png",
                    "Transparent forest bubble where stargazing happens from bed and every night feels like sleeping inside a sci-fi snow globe.",
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
                    "Mirage Moonbell Camp",
                    "/listings/tent.png",
                    "Desert glamping bell tent with sunset mountain silhouettes, firepit vibes, and the kind of soft lighting influencers pray for.",
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
                    "Poseidon's Neon Sleeper",
                    "/listings/underwater.png",
                    "An underwater suite wrapped in blue light and passing fish, built for guests who want sleepovers sponsored by Neptune.",
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
