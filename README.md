# LibreTranslate Java Simple Client  
**Java client for the LibreTranslate API**  
A lightweight, configurable client for interacting with LibreTranslate's machine translation service.  
**JDK 17+ required**  

---

## Installation  
### Maven  
```xml  
<dependency>  
    <groupId>com.vidigal.code</groupId>  
    <artifactId>libretranslate-simple-java</artifactId>  
    <version>1.0.0</version>  
</dependency>  
```  

### Gradle
```groovy  
implementation 'com.vidigal.code:libretranslate-simple-java-client:1.0.0'  
```  

---

## Configuration
Create a configuration using the builder pattern:
```java  

LibreTranslateConfig config = LibreTranslateConfig.builder()
        .apiUrl("https://libretranslate.com")
        .apiKey("your_api_key")
        .readtimeout(5000)    // socket timeout (ms)
        .timeout(5000)        // Connection (ms)  
        .maxRetries(3)       // Max retry attempts for failed requests  
        .build();
```  

---

## Basic Usage

### Service Factory Pattern
```java  
// Create service instance with API URL and key  
TranslatorService service = TranslatorService.create("https://libretranslate.com", "api_key");  

// Translate using service interface  
String result = service.translate("Welcome", "en", "de");  
System.out.println(result);  // Output: "Willkommen"  
```  

---

## Error Handling
All translation operations throw `TranslationException`:
```java  
try {  
    service.translate("", "en", "fr");  // Empty text  
} catch (TranslationException e) {  
    System.err.println("Translation failed: " + e.getMessage());  
}  
```  

## Technical Specifications
| Parameter      | Description                         | Default Value |  
|----------------|-------------------------------------|---------------|  
| `apiUrl`       | LibreTranslate API endpoint         | Required      |  
| `apiKey`       | Authentication key (if required)    | Required      |  
| `read timeout` | socket read timeout (ms)            | 5000          |
| `timeout`      | Connection timeout (ms)             | 5000          |  
| `maxRetries`   | Max retry attempts for failed calls | 3             |  

---

## Example Application
```java  
/**
 * Main class to demonstrate text translation using TranslatorService
 * and LibreTranslateClient.
 */
public static void main(String[] args) {

    // Create an instance of TranslatorService
    TranslatorService translator = TranslatorService.create(API, KEY);

    // Translate "Hello world" to Portuguese
    String result = translator.translate("Hello world", "pt");
    System.out.println(GREEN + "Translated to Portuguese: " + result + RESET);

    // Translate "Hello world" from English to Spanish
    String resultWithSource = translator.translate("Hello world", "en", "es");
    System.out.println(GREEN + "Translated to Spanish: " + resultWithSource + RESET);

    // Configure LibreTranslateClient with API settings
    LibreTranslateConfig config = LibreTranslateConfig.builder()
            .apiUrl(API)
            .apiKey(KEY)
            .readtimeout(1000)
            .timeout(1000)
            .maxRetries(3)
            .build();

    // Create an instance of LibreTranslateClient
    LibreTranslateClient client = new LibreTranslateClient(config);

    // Translate "Hello world" from English to Portuguese using LibreTranslateClient
    String resultWithConfig = client.translate("Hello world", "en", "pt");
    System.out.println(GREEN + "Translated to Portuguese Custom Config: " + resultWithConfig + RESET);
}
```  


# License

This project is licensed under the **MIT License**.

See the [LICENSE](https://github.com/Vidigal-code/libretranslate-simple-java/blob/main/License.mit) file for more details.


# License - API

This project is licensed api under the **MIT License**.

See the [LICENSE](https://github.com/LibreTranslate/LibreTranslate/blob/main/LICENSE) file for more details.


---




## Credits

- **Creator**: Kauan Vidigal
- **Translation API**: [LibreTranslate](https://libretranslate.com/)
- **Contributions**: Contributions are welcome! Feel free to fork the repository, open an issue, or submit a pull request for improvements or new features.

## Links
- [LibreTranslate API Documentation](https://libretranslate.com/docs)
- [LibreTranslate API GitHub](https://github.com/LibreTranslate/LibreTranslate)


