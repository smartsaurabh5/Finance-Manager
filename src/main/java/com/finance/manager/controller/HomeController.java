package com.finance.manager.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String home() {
        return """
                <!doctype html>
                <html>
                <head>
                  <title>Finance Manager API</title>
                  <style>
                    body {
                      margin: 0;
                      font-family: Arial, sans-serif;
                      background: #f6f8fb;
                      color: #172033;
                    }
                    main {
                      max-width: 920px;
                      margin: 48px auto;
                      padding: 0 24px;
                    }
                    h1 {
                      margin-bottom: 8px;
                      font-size: 34px;
                    }
                    .muted {
                      color: #5d6b82;
                      margin-bottom: 28px;
                    }
                    .grid {
                      display: grid;
                      grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
                      gap: 16px;
                    }
                    a.card {
                      display: block;
                      padding: 18px;
                      border: 1px solid #dce3ee;
                      border-radius: 8px;
                      background: #ffffff;
                      color: inherit;
                      text-decoration: none;
                    }
                    a.card:hover {
                      border-color: #4f7cff;
                    }
                    .title {
                      font-weight: 700;
                      margin-bottom: 6px;
                    }
                    code {
                      background: #eef2f7;
                      padding: 2px 6px;
                      border-radius: 5px;
                    }
                  </style>
                </head>
                <body>
                  <main>
                    <h1>Finance Manager API</h1>
                    <p class="muted">Backend is running. Use the API documentation or call the endpoints with Postman, Swagger, curl, or your frontend.</p>
                    <div class="grid">
                      <a class="card" href="/swagger-ui/index.html">
                        <div class="title">Swagger UI</div>
                        <div>Open interactive API documentation and test endpoints.</div>
                      </a>
                      <a class="card" href="/v3/api-docs">
                        <div class="title">OpenAPI JSON</div>
                        <div>Raw API schema for tools and generated clients.</div>
                      </a>
                      <a class="card" href="/h2-console">
                        <div class="title">H2 Console</div>
                        <div>Inspect the local in-memory database.</div>
                      </a>
                    </div>
                    <p class="muted">Protected routes need login first: <code>POST /api/auth/register</code>, then <code>POST /api/auth/login</code>.</p>
                  </main>
                </body>
                </html>
                """;
    }
}
