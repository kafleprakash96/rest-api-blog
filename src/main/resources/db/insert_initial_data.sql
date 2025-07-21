-- src/main/resources/db/migration/V2__Insert_initial_data.sql
-- Insert default admin user (password: admin123 - BCrypt encoded)
INSERT INTO users (username, email, password, first_name, last_name, role, bio, created_at, updated_at)
VALUES (
           'admin',
           'admin@blog.com',
           '$2a$10$rKYILZVc3a5UUOcAhsTGIOlhNe4vSvg4qF7LqwRKJQbSUKHqfBZGC',
           'Admin',
           'User',
           'ADMIN',
           'Administrator of the blog system',
           NOW(),
           NOW()
       );

-- Insert sample tags
INSERT INTO tags (name, slug, description, color, created_at, updated_at) VALUES
                                                                              ('Spring Boot', 'spring-boot', 'Java framework for building applications', '#6db33f', NOW(), NOW()),
                                                                              ('Angular', 'angular', 'TypeScript-based web application framework', '#dd0031', NOW(), NOW()),
                                                                              ('JavaScript', 'javascript', 'Programming language for web development', '#f7df1e', NOW(), NOW()),
                                                                              ('Java', 'java', 'Object-oriented programming language', '#007396', NOW(), NOW()),
                                                                              ('TypeScript', 'typescript', 'Typed superset of JavaScript', '#3178c6', NOW(), NOW()),
                                                                              ('REST API', 'rest-api', 'Representational State Transfer API design', '#009688', NOW(), NOW()),
                                                                              ('Database', 'database', 'Data storage and management systems', '#ff9800', NOW(), NOW()),
                                                                              ('Tutorial', 'tutorial', 'Step-by-step learning content', '#9c27b0', NOW(), NOW()),
                                                                              ('Best Practices', 'best-practices', 'Recommended approaches and methods', '#2196f3', NOW(), NOW()),
                                                                              ('Security', 'security', 'Application and data security topics', '#f44336', NOW(), NOW());

-- Insert sample blog posts
INSERT INTO posts (
    title, slug, excerpt, content, author_id, status, published_date,
    meta_description, meta_keywords, reading_time_minutes, is_featured, created_at, updated_at
) VALUES (
             'Getting Started with Spring Boot: A Comprehensive Guide',
             'getting-started-with-spring-boot-comprehensive-guide',
             'Learn how to build modern Java applications with Spring Boot. This comprehensive guide covers everything from setup to deployment.',
             '<h2>Introduction to Spring Boot</h2>
             <p>Spring Boot is a powerful framework that makes it easy to create stand-alone, production-grade Spring-based applications with minimal configuration.</p>

             <h3>Why Choose Spring Boot?</h3>
             <ul>
                 <li>Auto-configuration reduces boilerplate code</li>
                 <li>Embedded servers for easy deployment</li>
                 <li>Production-ready features out of the box</li>
                 <li>Extensive ecosystem and community support</li>
             </ul>

             <h3>Setting Up Your First Spring Boot Project</h3>
             <p>The easiest way to start a Spring Boot project is using Spring Initializr:</p>

             <pre><code>curl https://start.spring.io/starter.zip \
             -d dependencies=web,data-jpa,h2 \
             -d type=maven-project \
             -d baseDir=my-spring-boot-app \
             -o my-spring-boot-app.zip</code></pre>

             <h3>Creating Your First Controller</h3>
             <p>Here''s a simple REST controller example:</p>

             <pre><code>@RestController
         public class HelloController {

             @GetMapping("/hello")
             public String hello() {
                 return "Hello, Spring Boot!";
             }
         }</code></pre>

             <h3>Conclusion</h3>
             <p>Spring Boot simplifies Java development by providing sensible defaults and auto-configuration. Start building amazing applications today!</p>',
             1,
             'PUBLISHED',
             NOW(),
             'Learn Spring Boot from scratch with this comprehensive guide covering setup, configuration, and best practices.',
             'spring boot, java, tutorial, web development, rest api',
             8,
             TRUE,
             NOW(),
             NOW()
         ), (
             'Building Modern Web Apps with Angular and TypeScript',
             'building-modern-web-apps-angular-typescript',
             'Discover how to create scalable and maintainable web applications using Angular framework and TypeScript.',
             '<h2>Introduction to Angular</h2>
             <p>Angular is a platform and framework for building single-page client applications using HTML and TypeScript.</p>

             <h3>Key Features of Angular</h3>
             <ul>
                 <li>Component-based architecture</li>
                 <li>Two-way data binding</li>
                 <li>Dependency injection</li>
                 <li>TypeScript support</li>
                 <li>Powerful CLI tools</li>
             </ul>

             <h3>Setting Up Angular Development Environment</h3>
             <p>Install Angular CLI globally:</p>

             <pre><code>npm install -g @angular/cli
         ng new my-angular-app
         cd my-angular-app
         ng serve</code></pre>

             <h3>Creating Components</h3>
             <p>Generate a new component:</p>

             <pre><code>ng generate component my-component</code></pre>

             <h3>Best Practices</h3>
             <ul>
                 <li>Use OnPush change detection strategy</li>
                 <li>Implement lazy loading for routes</li>
                 <li>Follow Angular style guide</li>
                 <li>Use reactive forms</li>
             </ul>',
             1,
             'PUBLISHED',
             DATE_SUB(NOW(), INTERVAL 2 DAY),
             'Learn to build modern web applications with Angular and TypeScript. Complete guide with examples and best practices.',
             'angular, typescript, web development, spa, frontend',
             12,
             TRUE,
             DATE_SUB(NOW(), INTERVAL 2 DAY),
             DATE_SUB(NOW(), INTERVAL 2 DAY)
         ), (
             'RESTful API Design Best Practices',
             'restful-api-design-best-practices',
             'Learn the essential principles and best practices for designing robust and scalable REST APIs.',
             '<h2>What is a RESTful API?</h2>
             <p>REST (Representational State Transfer) is an architectural style for designing networked applications.</p>

             <h3>Core Principles</h3>
             <ul>
                 <li>Stateless communication</li>
                 <li>Resource-based URLs</li>
                 <li>HTTP methods for operations</li>
                 <li>JSON for data exchange</li>
             </ul>

             <h3>HTTP Methods</h3>
             <ul>
                 <li><strong>GET</strong>: Retrieve data</li>
                 <li><strong>POST</strong>: Create new resources</li>
                 <li><strong>PUT</strong>: Update existing resources</li>
                 <li><strong>DELETE</strong>: Remove resources</li>
             </ul>

             <h3>Status Codes</h3>
             <ul>
                 <li>200 OK - Success</li>
                 <li>201 Created - Resource created</li>
                 <li>400 Bad Request - Client error</li>
                 <li>404 Not Found - Resource not found</li>
                 <li>500 Internal Server Error - Server error</li>
             </ul>

             <h3>Best Practices</h3>
             <ul>
                 <li>Use nouns for resource names</li>
                 <li>Implement proper error handling</li>
                 <li>Version your APIs</li>
                 <li>Use HTTPS</li>
                 <li>Implement rate limiting</li>
             </ul>',
             1,
             'PUBLISHED',
             DATE_SUB(NOW(), INTERVAL 5 DAY),
             'Master REST API design with these essential best practices and principles for building robust web services.',
             'rest api, web services, http, json, api design',
             10,
             FALSE,
             DATE_SUB(NOW(), INTERVAL 5 DAY),
             DATE_SUB(NOW(), INTERVAL 5 DAY)
         ), (
             'Database Security: Protecting Your Data',
             'database-security-protecting-your-data',
             'Essential strategies and techniques for securing your database and protecting sensitive information.',
             '<h2>Why Database Security Matters</h2>
             <p>Databases contain valuable and sensitive information that must be protected from unauthorized access and breaches.</p>

             <h3>Common Security Threats</h3>
             <ul>
                 <li>SQL injection attacks</li>
                 <li>Unauthorized access</li>
                 <li>Data breaches</li>
                 <li>Privilege escalation</li>
             </ul>

             <h3>Security Best Practices</h3>
             <ul>
                 <li>Use parameterized queries</li>
                 <li>Implement proper authentication</li>
                 <li>Encrypt sensitive data</li>
                 <li>Regular security audits</li>
                 <li>Principle of least privilege</li>
             </ul>

             <h3>Preventing SQL Injection</h3>
             <p>Always use prepared statements:</p>

             <pre><code>// Bad - vulnerable to SQL injection
         String query = "SELECT * FROM users WHERE id = " + userId;

         // Good - using prepared statement
         String query = "SELECT * FROM users WHERE id = ?";
         PreparedStatement stmt = connection.prepareStatement(query);
         stmt.setLong(1, userId);</code></pre>

             <h3>Data Encryption</h3>
             <ul>
                 <li>Encrypt data at rest</li>
                 <li>Encrypt data in transit</li>
                 <li>Use strong encryption algorithms</li>
                 <li>Manage encryption keys securely</li>
             </ul>',
             1,
             'DRAFT',
             NULL,
             'Learn essential database security practices to protect your data from threats and unauthorized access.',
             'database security, sql injection, encryption, data protection',
             15,
             FALSE,
             DATE_SUB(NOW(), INTERVAL 1 DAY),
             DATE_SUB(NOW(), INTERVAL 1 DAY)
         );

-- Link posts with tags
INSERT INTO post_tags (post_id, tag_id) VALUES
-- Spring Boot post
(1, 1), -- Spring Boot
(1, 4), -- Java
(1, 6), -- REST API
(1, 8), -- Tutorial

-- Angular post
(2, 2), -- Angular
(2, 3), -- JavaScript
(2, 5), -- TypeScript
(2, 8), -- Tutorial

-- REST API post
(3, 6), -- REST API
(3, 9), -- Best Practices
(3, 8), -- Tutorial

-- Database Security post
(4, 7), -- Database
(4, 10), -- Security
(4, 9); -- Best Practices
