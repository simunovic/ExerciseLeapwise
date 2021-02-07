# RssFeedAnalysis

Service is used for analysing RSS feeds and finding topics that are most common.

## Specification

There are two api endpoints exposed:

```
/analyse/new
```

```
/frequency/{id}
```

### API Definition: 

```
/analyse/new
```

This endpoint takes at least two RSS URLs as parameters (more are posible) e.g:
```
{
  "urls":
    [
      "https://rss.nytimes.com/services/xml/rss/nyt/Europe.xml",
      "https://www.thesundaily.my/rss/world"
    ]
}
```

Endpoint returns analysis global unique identifier.

### API Definition: 

```
/frequency/{id}
```

This endpoint takes analysis unique identifier and return elements with most matches.
Additionaly, original news header and link are displayed.

Response in JSON format:
 
```
[
  {
    "name": String,
    "relatedFeeds": 
    [
      {
        "header": String,
        "link": String,
      }, ...
    ]
  }, ...
]
```

## Additional Information

Running the exercise with maven

```mvn spring-boot:run```
