#!/bin/bash

BASE_URL="http://localhost:8080/api/v1"

echo "🚀 Setting up blog data..."

# 1. Login and get token
echo "📝 Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/signin" \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "prakash12"}')

JWT_TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.data.token')

if [ "$JWT_TOKEN" = "null" ]; then
  echo "❌ Login failed. Please check your credentials and server status."
  exit 1
fi

echo "✅ Login successful! Token obtained."

# 2. Create comprehensive blog posts
echo "📚 Creating sample blog posts..."

# Spring Boot Post
curl -s -X POST "$BASE_URL/posts" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "title": "Getting Started with Spring Boot: A Complete Guide",
    "excerpt": "Learn how to build powerful Java applications with Spring Boot. This comprehensive tutorial covers everything from project setup to deployment.",
    "content": "<h2>Introduction to Spring Boot</h2><p>Spring Boot has revolutionized Java development by providing auto-configuration and reducing boilerplate code.</p><h3>What You'\''ll Learn</h3><ul><li>Setting up a Spring Boot project</li><li>Creating REST APIs</li><li>Database integration with JPA</li><li>Security implementation</li></ul><h3>Project Setup</h3><pre><code>curl https://start.spring.io/starter.zip -d dependencies=web,data-jpa,security</code></pre><h3>Your First Controller</h3><pre><code>@RestController\n@RequestMapping(\"/api\")\npublic class HelloController {\n    @GetMapping(\"/hello\")\n    public ResponseEntity&lt;String&gt; hello() {\n        return ResponseEntity.ok(\"Hello, Spring Boot!\");\n    }\n}</code></pre>",
    "readingTimeMinutes": 8,
    "status": "PUBLISHED",
    "metaDescription": "Complete Spring Boot tutorial covering project setup, REST APIs, and deployment.",
    "metaKeywords": "spring boot, java, tutorial, rest api, web development",
    "tagNames": ["Spring Boot", "Java", "Tutorial", "REST API"],
    "isFeatured": true,
    "allowComments": true
  }' > /dev/null

# Angular Post
curl -s -X POST "$BASE_URL/posts" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "title": "Building Modern Web Apps with Angular and TypeScript",
    "excerpt": "Master Angular development with TypeScript. Learn component architecture, services, routing, and best practices.",
    "content": "<h2>Why Angular and TypeScript?</h2><p>Angular with TypeScript provides a robust foundation for building large-scale web applications.</p><h3>Key Benefits</h3><ul><li>Type Safety</li><li>Better IDE support</li><li>Scalability</li></ul><h3>Getting Started</h3><pre><code>npm install -g @angular/cli\nng new my-app\ncd my-app\nng serve</code></pre><h3>Component Example</h3><pre><code>@Component({\n  selector: '\''app-user'\'',\n  template: `&lt;h2&gt;{{user.name}}&lt;/h2&gt;`\n})\nexport class UserComponent {\n  user = { name: '\''John Doe'\'' };\n}</code></pre>",
    "readingTimeMinutes": 12,
    "status": "PUBLISHED",
    "metaDescription": "Learn Angular and TypeScript development with this comprehensive guide.",
    "metaKeywords": "angular, typescript, web development, components, services",
    "tagNames": ["Angular", "TypeScript", "Frontend", "Tutorial"],
    "isFeatured": true,
    "allowComments": true
  }' > /dev/null

# Database Design Post
curl -s -X POST "$BASE_URL/posts" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "title": "Database Design Best Practices for Web Applications",
    "excerpt": "Learn essential database design principles, normalization techniques, and optimization strategies.",
    "content": "<h2>Database Design Fundamentals</h2><p>Proper database design is crucial for application performance and scalability.</p><h3>Key Principles</h3><ul><li>Normalization</li><li>Referential Integrity</li><li>Indexing</li><li>Data Types</li></ul><h3>Normalization Example</h3><pre><code>-- First Normal Form (1NF)\nCREATE TABLE users (\n    id INT PRIMARY KEY,\n    name VARCHAR(100)\n);\n\nCREATE TABLE user_hobbies (\n    user_id INT,\n    hobby VARCHAR(50),\n    FOREIGN KEY (user_id) REFERENCES users(id)\n);</code></pre><h3>Indexing Strategy</h3><pre><code>CREATE INDEX idx_posts_created_at ON posts(created_at);\nCREATE INDEX idx_posts_status_date ON posts(status, created_at);</code></pre>",
    "readingTimeMinutes": 15,
    "status": "PUBLISHED",
    "metaDescription": "Master database design with normalization, indexing, and optimization techniques.",
    "metaKeywords": "database design, normalization, sql, indexing, performance",
    "tagNames": ["Database", "SQL", "Performance", "Best Practices"],
    "isFeatured": false,
    "allowComments": true
  }' > /dev/null

# API Security Post
curl -s -X POST "$BASE_URL/posts" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "title": "API Security: Protecting Your REST Endpoints",
    "excerpt": "Comprehensive guide to securing REST APIs including authentication, authorization, and common vulnerabilities.",
    "content": "<h2>API Security Essentials</h2><p>APIs are prime targets for attackers. Implementing proper security is essential.</p><h3>Common Threats</h3><ul><li>Injection Attacks</li><li>Broken Authentication</li><li>Data Exposure</li><li>Rate Limiting Issues</li></ul><h3>JWT Authentication</h3><pre><code>@PostMapping(\"/login\")\npublic ResponseEntity&lt;JwtResponse&gt; login(@RequestBody LoginRequest request) {\n    Authentication auth = authManager.authenticate(\n        new UsernamePasswordAuthenticationToken(\n            request.getUsername(), request.getPassword()\n        )\n    );\n    String jwt = jwtProvider.generateToken(auth);\n    return ResponseEntity.ok(new JwtResponse(jwt));\n}</code></pre><h3>Security Checklist</h3><ul><li>✅ Use HTTPS</li><li>✅ Implement authentication</li><li>✅ Validate inputs</li><li>✅ Rate limiting</li><li>✅ Security headers</li></ul>",
    "readingTimeMinutes": 18,
    "status": "PUBLISHED",
    "metaDescription": "Complete API security guide covering authentication, authorization, and vulnerability protection.",
    "metaKeywords": "api security, jwt, authentication, spring security, rest api",
    "tagNames": ["Security", "API", "Spring Security", "Best Practices"],
    "isFeatured": true,
    "allowComments": true
  }' > /dev/null

# Draft Post
curl -s -X POST "$BASE_URL/posts" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "title": "React vs Angular: Choosing the Right Frontend Framework",
    "excerpt": "Detailed comparison between React and Angular frameworks to help you make an informed decision.",
    "content": "<h2>The Framework Decision</h2><p>Choosing between React and Angular is a common dilemma for developers.</p><h3>React Advantages</h3><ul><li>Flexibility</li><li>Large ecosystem</li><li>React Native</li><li>Easy learning curve</li></ul><h3>Angular Advantages</h3><ul><li>Complete framework</li><li>TypeScript built-in</li><li>Enterprise ready</li><li>Powerful CLI</li></ul><p><em>This post is being written and will be published soon...</em></p>",
    "readingTimeMinutes": 10,
    "status": "DRAFT",
    "metaDescription": "React vs Angular comparison guide for choosing the right frontend framework.",
    "metaKeywords": "react vs angular, frontend frameworks, javascript, typescript",
    "tagNames": ["React", "Angular", "Frontend", "Comparison"],
    "isFeatured": false,
    "allowComments": true
  }' > /dev/null

echo "✅ Sample posts created!"

# 3. Verify creation
echo "📊 Verifying setup..."
PUBLISHED_COUNT=$(curl -s -X GET "$BASE_URL/posts/published?pageSize=100" | jq -r '.data.totalElements // 0')
TOTAL_COUNT=$(curl -s -X GET "$BASE_URL/posts?pageSize=100" -H "Authorization: Bearer $JWT_TOKEN" | jq -r '.data.totalElements // 0')

echo "✅ Published posts: $PUBLISHED_COUNT"
echo "✅ Total posts: $TOTAL_COUNT"
echo "✅ Tags created automatically from posts"

echo ""
echo "🎉 Blog setup complete!"
echo "📱 Visit http://localhost:4200 to see your blog"
echo "🔧 Visit http://localhost:4200/admin to manage posts"
echo "📚 API docs: http://localhost:8080/swagger-ui.html"
echo ""
echo "📝 Admin credentials:"
echo "   Username: admin"
echo "   Password: admin123"
