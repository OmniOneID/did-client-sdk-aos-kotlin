---
puppeteer:
    pdf:
        format: A4
        displayHeaderFooter: true
        landscape: false
        scale: 0.8
        margin:
            top: 1.2cm
            right: 1cm
            bottom: 1cm
            left: 1cm
    image:
        quality: 100
        fullPage: false
---

Android Communication API
==

- Subject: Communication
- Author: Sangjun Kim
- Date: 2025-03-13
- Version: v1.0.0

| Version | Date       | Changes                  |
| ------- | ---------- | ------------------------ |
| v1.0.0  | 2025-03-13 | Initial version          |


<div style="page-break-after: always;"></div>

# Table of Contents
- [APIs](#api-list)
  - [1. makeHttpRequest](#1-makehttprequest)


# API List
### 1. makeHttpRequest

#### Description
`Provides HTTP request and response functionality.`

#### Declaration

```kotlin
fun makeHttpRequest(urlString: String, method: String, payload: String): String
```

#### Parameters

| Parameter | Type   | Description                | **M/O** | **Note** |
|-----------|--------|----------------------------|---------|---------|
| urlString    | String    | Server URL |M| |
| method    | String    | HTTP Method |M| |
| payload    | String    | Request data |M| |

#### Returns

| Type | Description                |**M/O** | **Note** |
|------|----------------------------|---------|-------------|
| String  | Response data |M| |

#### Usage
```java
HttpUrlConnectionTask httpFunc = new HttpUrlConnectionTask();
String response = httpFunc.makeHttpRequest("http://opendid/api/v1", "POST", requestData);
```

<br>
