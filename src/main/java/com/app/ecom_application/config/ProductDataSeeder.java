package com.app.ecom_application.config;

import com.app.ecom_application.Model.Product;
import com.app.ecom_application.Repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ProductDataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    public ProductDataSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {

        // Prevent duplicate data whenever the application restarts
        if (productRepository.count() > 0) {
            System.out.println("Products already exist. Skipping product seeding.");
            return;
        }

        List<Product> products = List.of(

                // ==================== ELECTRONICS ====================

                new Product(
                        1L,
                        "Apple iPhone 15",
                        "Apple iPhone 15 with A16 Bionic chip and 48MP camera.",
                        new BigDecimal("69999.00"),
                        25,
                        "Electronics",
                        "https://images.unsplash.com/photo-1592286927505-2fd9f07d1d3e",
                        true,
                        null,
                        null
                ),

                new Product(
                        2L,
                        "Samsung Galaxy S24",
                        "Samsung flagship smartphone with AMOLED display.",
                        new BigDecimal("74999.00"),
                        20,
                        "Electronics",
                        "https://images.unsplash.com/photo-1610945415295-d9bbf37a9a32",
                        true,
                        null,
                        null
                ),

                new Product(
                        3L,
                        "Sony WH-1000XM5",
                        "Premium wireless noise cancelling headphones.",
                        new BigDecimal("29999.00"),
                        30,
                        "Electronics",
                        "https://images.unsplash.com/photo-1546435770-a3e426bf472b",
                        true,
                        null,
                        null
                ),

                new Product(
                        4L,
                        "Apple AirPods Pro",
                        "Wireless earbuds with active noise cancellation.",
                        new BigDecimal("24999.00"),
                        35,
                        "Electronics",
                        "https://images.unsplash.com/photo-1600294037681-c80b4cb5b434",
                        true,
                        null,
                        null
                ),

                new Product(
                        5L,
                        "Dell Inspiron 15",
                        "15-inch laptop suitable for work and everyday computing.",
                        new BigDecimal("58999.00"),
                        15,
                        "Electronics",
                        "https://images.unsplash.com/photo-1593642532400-2682810df593",
                        true,
                        null,
                        null
                ),

                // ==================== FASHION ====================

                new Product(
                        6L,
                        "Men's Casual T-Shirt",
                        "Premium cotton casual t-shirt for everyday wear.",
                        new BigDecimal("799.00"),
                        100,
                        "Fashion",
                        "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab",
                        true,
                        null,
                        null
                ),

                new Product(
                        7L,
                        "Men's Denim Jacket",
                        "Classic blue denim jacket with a modern fit.",
                        new BigDecimal("2499.00"),
                        50,
                        "Fashion",
                        "https://images.unsplash.com/photo-1551028719-00167b16eac5",
                        true,
                        null,
                        null
                ),

                new Product(
                        8L,
                        "Women's Summer Dress",
                        "Lightweight floral summer dress.",
                        new BigDecimal("1899.00"),
                        45,
                        "Fashion",
                        "https://images.unsplash.com/photo-1496747611176-843222e1e57c",
                        true,
                        null,
                        null
                ),

                new Product(
                        9L,
                        "Women's Handbag",
                        "Stylish leather handbag suitable for everyday use.",
                        new BigDecimal("2999.00"),
                        40,
                        "Fashion",
                        "https://images.unsplash.com/photo-1584917865442-de89df76afd3",
                        true,
                        null,
                        null
                ),

                new Product(
                        10L,
                        "Running Sneakers",
                        "Lightweight running sneakers with cushioned soles.",
                        new BigDecimal("3499.00"),
                        60,
                        "Fashion",
                        "https://images.unsplash.com/photo-1542291026-7eec264c27ff",
                        true,
                        null,
                        null
                ),

                // ==================== HOME & KITCHEN ====================

                new Product(
                        11L,
                        "Coffee Maker",
                        "Automatic coffee maker for freshly brewed coffee.",
                        new BigDecimal("4999.00"),
                        25,
                        "Home & Kitchen",
                        "https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6",
                        true,
                        null,
                        null
                ),

                new Product(
                        12L,
                        "Non-Stick Cookware Set",
                        "Premium non-stick cookware set for everyday cooking.",
                        new BigDecimal("3999.00"),
                        30,
                        "Home & Kitchen",
                        "https://images.unsplash.com/photo-1556911220-e15b29be8c8f",
                        true,
                        null,
                        null
                ),

                new Product(
                        13L,
                        "Electric Kettle",
                        "Fast boiling electric kettle with automatic shutoff.",
                        new BigDecimal("1499.00"),
                        55,
                        "Home & Kitchen",
                        "https://images.unsplash.com/photo-1594213114663-d94db9b1718d",
                        true,
                        null,
                        null
                ),

                new Product(
                        14L,
                        "Table Lamp",
                        "Modern LED table lamp for bedrooms and offices.",
                        new BigDecimal("1299.00"),
                        40,
                        "Home & Kitchen",
                        "https://images.unsplash.com/photo-1507473885765-e6ed057f782c",
                        true,
                        null,
                        null
                ),

                new Product(
                        15L,
                        "Air Fryer",
                        "Digital air fryer with multiple cooking modes.",
                        new BigDecimal("5999.00"),
                        20,
                        "Home & Kitchen",
                        "https://images.unsplash.com/photo-1585515320310-259814833e62",
                        true,
                        null,
                        null
                ),

                // ==================== BOOKS ====================

                new Product(
                        16L,
                        "Clean Code",
                        "A practical guide to writing clean and maintainable software.",
                        new BigDecimal("699.00"),
                        50,
                        "Books",
                        "https://images.unsplash.com/photo-1544947950-fa07a98d237f",
                        true,
                        null,
                        null
                ),

                new Product(
                        17L,
                        "Effective Java",
                        "Best practices and programming techniques for Java developers.",
                        new BigDecimal("899.00"),
                        40,
                        "Books",
                        "https://images.unsplash.com/photo-1515879218367-8466d910aaa4",
                        true,
                        null,
                        null
                ),

                new Product(
                        18L,
                        "Atomic Habits",
                        "A practical guide to building better habits.",
                        new BigDecimal("499.00"),
                        70,
                        "Books",
                        "https://images.unsplash.com/photo-1543002588-bfa74002ed7e",
                        true,
                        null,
                        null
                ),

                new Product(
                        19L,
                        "The Psychology of Money",
                        "Insights into money, investing and financial behavior.",
                        new BigDecimal("399.00"),
                        65,
                        "Books",
                        "https://images.unsplash.com/photo-1554224155-6726b3ff858f",
                        true,
                        null,
                        null
                ),

                new Product(
                        20L,
                        "System Design Interview",
                        "Guide to solving system design interview problems.",
                        new BigDecimal("799.00"),
                        35,
                        "Books",
                        "https://images.unsplash.com/photo-1495446815901-a7297e633e8d",
                        true,
                        null,
                        null
                ),

                // ==================== SPORTS ====================

                new Product(
                        21L,
                        "Football",
                        "Professional size football suitable for training and matches.",
                        new BigDecimal("999.00"),
                        50,
                        "Sports",
                        "https://images.unsplash.com/photo-1579952363873-27f3bade9f55",
                        true,
                        null,
                        null
                ),

                new Product(
                        22L,
                        "Cricket Bat",
                        "English willow cricket bat for professional players.",
                        new BigDecimal("7999.00"),
                        20,
                        "Sports",
                        "https://images.unsplash.com/photo-1531415074968-036ba1b575da",
                        true,
                        null,
                        null
                ),

                new Product(
                        23L,
                        "Basketball",
                        "Official size basketball with durable rubber construction.",
                        new BigDecimal("1299.00"),
                        45,
                        "Sports",
                        "https://images.unsplash.com/photo-1546519638-68e109498ffc",
                        true,
                        null,
                        null
                ),

                new Product(
                        24L,
                        "Yoga Mat",
                        "Non-slip yoga mat with comfortable cushioning.",
                        new BigDecimal("899.00"),
                        80,
                        "Sports",
                        "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f",
                        true,
                        null,
                        null
                ),

                new Product(
                        25L,
                        "Dumbbell Set",
                        "Adjustable dumbbell set for home workouts.",
                        new BigDecimal("3999.00"),
                        30,
                        "Sports",
                        "https://images.unsplash.com/photo-1583454110551-21f2fa2afe61",
                        true,
                        null,
                        null
                ),

                // ==================== BEAUTY ====================

                new Product(
                        26L,
                        "Face Moisturizer",
                        "Hydrating moisturizer suitable for daily skincare.",
                        new BigDecimal("599.00"),
                        100,
                        "Beauty",
                        "https://images.unsplash.com/photo-1556229010-6c3f2c9ca5f8",
                        true,
                        null,
                        null
                ),

                new Product(
                        27L,
                        "Vitamin C Serum",
                        "Brightening vitamin C serum for daily skincare.",
                        new BigDecimal("799.00"),
                        75,
                        "Beauty",
                        "https://images.unsplash.com/photo-1620916566398-39f1143ab7be",
                        true,
                        null,
                        null
                ),

                new Product(
                        28L,
                        "Perfume",
                        "Long-lasting fragrance with a fresh and elegant aroma.",
                        new BigDecimal("1999.00"),
                        50,
                        "Beauty",
                        "https://images.unsplash.com/photo-1541643600914-78b084683601",
                        true,
                        null,
                        null
                ),

                new Product(
                        29L,
                        "Hair Dryer",
                        "Powerful hair dryer with multiple heat settings.",
                        new BigDecimal("1799.00"),
                        40,
                        "Beauty",
                        "https://images.unsplash.com/photo-1522338140262-f46f5913618a",
                        true,
                        null,
                        null
                ),

                new Product(
                        30L,
                        "Makeup Brush Set",
                        "Professional makeup brush set with multiple brushes.",
                        new BigDecimal("1299.00"),
                        60,
                        "Beauty",
                        "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9",
                        true,
                        null,
                        null
                ),

                // ==================== GROCERY ====================

                new Product(
                        31L,
                        "Organic Rice",
                        "Premium organic basmati rice.",
                        new BigDecimal("799.00"),
                        100,
                        "Grocery",
                        "https://images.unsplash.com/photo-1586201375761-83865001e31c",
                        true,
                        null,
                        null
                ),

                new Product(
                        32L,
                        "Organic Honey",
                        "Natural organic honey with no added preservatives.",
                        new BigDecimal("499.00"),
                        80,
                        "Grocery",
                        "https://images.unsplash.com/photo-1587049352846-4a222e784d38",
                        true,
                        null,
                        null
                ),

                new Product(
                        33L,
                        "Green Tea",
                        "Premium green tea leaves for a refreshing drink.",
                        new BigDecimal("299.00"),
                        90,
                        "Grocery",
                        "https://images.unsplash.com/photo-1556679343-c7306c1976bc",
                        true,
                        null,
                        null
                ),

                new Product(
                        34L,
                        "Almonds",
                        "Premium roasted almonds packed for freshness.",
                        new BigDecimal("699.00"),
                        70,
                        "Grocery",
                        "https://images.unsplash.com/photo-1508061253366-f7da158b6d46",
                        true,
                        null,
                        null
                ),

                new Product(
                        35L,
                        "Dark Chocolate",
                        "Premium dark chocolate with rich cocoa flavor.",
                        new BigDecimal("249.00"),
                        100,
                        "Grocery",
                        "https://images.unsplash.com/photo-1548907040-4d42a42f3b8f",
                        true,
                        null,
                        null
                ),

                // ==================== TOYS ====================

                new Product(
                        36L,
                        "Remote Control Car",
                        "Fast remote control racing car for kids.",
                        new BigDecimal("1499.00"),
                        35,
                        "Toys",
                        "https://images.unsplash.com/photo-1594787318286-3d835c1d207f",
                        true,
                        null,
                        null
                ),

                new Product(
                        37L,
                        "Building Blocks",
                        "Creative building blocks set for children.",
                        new BigDecimal("999.00"),
                        50,
                        "Toys",
                        "https://images.unsplash.com/photo-1587654780291-39c9404d746b",
                        true,
                        null,
                        null
                ),

                new Product(
                        38L,
                        "Teddy Bear",
                        "Soft and cuddly teddy bear for children.",
                        new BigDecimal("799.00"),
                        60,
                        "Toys",
                        "https://images.unsplash.com/photo-1559454403-b8fb88521f11",
                        true,
                        null,
                        null
                ),

                new Product(
                        39L,
                        "Puzzle Game",
                        "Educational puzzle game for developing problem-solving skills.",
                        new BigDecimal("499.00"),
                        75,
                        "Toys",
                        "https://images.unsplash.com/photo-1618842676088-c4d48a6a7c9d",
                        true,
                        null,
                        null
                ),

                new Product(
                        40L,
                        "Board Game",
                        "Family board game suitable for multiple players.",
                        new BigDecimal("899.00"),
                        40,
                        "Toys",
                        "https://images.unsplash.com/photo-1606503153255-59d8b8b821b1",
                        true,
                        null,
                        null
                ),

                // ==================== AUTOMOTIVE ====================

                new Product(
                        41L,
                        "Car Phone Holder",
                        "Universal dashboard phone holder for cars.",
                        new BigDecimal("599.00"),
                        80,
                        "Automotive",
                        "https://images.unsplash.com/photo-1502877338535-766e1452684a",
                        true,
                        null,
                        null
                ),

                new Product(
                        42L,
                        "Car Vacuum Cleaner",
                        "Compact vacuum cleaner designed for car interiors.",
                        new BigDecimal("1999.00"),
                        35,
                        "Automotive",
                        "https://images.unsplash.com/photo-1542367597-8844c7e9a6c8",
                        true,
                        null,
                        null
                ),

                new Product(
                        43L,
                        "Car Air Freshener",
                        "Long-lasting car air freshener with a pleasant fragrance.",
                        new BigDecimal("299.00"),
                        100,
                        "Automotive",
                        "https://images.unsplash.com/photo-1493238792000-8113da705763",
                        true,
                        null,
                        null
                ),

                new Product(
                        44L,
                        "Car Cleaning Kit",
                        "Complete cleaning kit for maintaining your car.",
                        new BigDecimal("1299.00"),
                        45,
                        "Automotive",
                        "https://images.unsplash.com/photo-1607860108855-64acf2078ed9",
                        true,
                        null,
                        null
                ),

                new Product(
                        45L,
                        "LED Headlight",
                        "Bright LED headlights for improved visibility.",
                        new BigDecimal("2499.00"),
                        30,
                        "Automotive",
                        "https://images.unsplash.com/photo-1492144534655-ae79c964c9d7",
                        true,
                        null,
                        null
                ),

                // ==================== FURNITURE ====================

                new Product(
                        46L,
                        "Office Chair",
                        "Ergonomic office chair with adjustable height.",
                        new BigDecimal("6999.00"),
                        20,
                        "Furniture",
                        "https://images.unsplash.com/photo-1580480055273-228ff5388ef8",
                        true,
                        null,
                        null
                ),

                new Product(
                        47L,
                        "Wooden Study Table",
                        "Modern wooden study table for home offices.",
                        new BigDecimal("5999.00"),
                        15,
                        "Furniture",
                        "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd",
                        true,
                        null,
                        null
                ),

                new Product(
                        48L,
                        "Bookshelf",
                        "Five-tier wooden bookshelf with modern design.",
                        new BigDecimal("4499.00"),
                        25,
                        "Furniture",
                        "https://images.unsplash.com/photo-1594620302200-9a762244a156",
                        true,
                        null,
                        null
                ),

                new Product(
                        49L,
                        "Sofa",
                        "Comfortable three-seater sofa for living rooms.",
                        new BigDecimal("24999.00"),
                        10,
                        "Furniture",
                        "https://images.unsplash.com/photo-1555041469-a586c61ea9bc",
                        true,
                        null,
                        null
                ),

                new Product(
                        50L,
                        "Bedside Table",
                        "Compact bedside table with storage drawer.",
                        new BigDecimal("2499.00"),
                        30,
                        "Furniture",
                        "https://images.unsplash.com/photo-1532372320572-cda25653a26d",
                        true,
                        null,
                        null
                )
        );

        productRepository.saveAll(products);

        System.out.println("==========================================");
        System.out.println("Successfully seeded " + products.size() + " products!");
        System.out.println("==========================================");
    }
}