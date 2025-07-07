Let's see how authentication works here

- SecurityConfig enables HTTP Basic authentication. Only /users/signup and /health are open to everyone.
    All other endpoints require authentication via HTTP Basic.
- CustomUserDetailsService class is annotated with @Service, making it a Spring bean.
  Spring Security automatically looks for a bean that implements UserDetailsService in the application context.
- When a user tries to authenticate, Spring Security:
  1. Extracts the username from the request.
  2. Calls the UserDetailsService.loadUserByUsername(username) method (your implementation).
  3. Gets the UserDetails object (with username, password, roles).
  4. Compares the password from the request with the password from the UserDetails.
  5. If they match, authentication succeeds.


ObjectMapper mapper = new ObjectMapper();

// Convert List<String> to JSON String
String amenitiesJson = mapper.writeValueAsString(Arrays.asList("WiFi", "AC", "TV"));
room.setAmenities(amenitiesJson);

// Convert JSON String back to List<String>
List<String> amenitiesList = mapper.readValue(room.getAmenities(), new TypeReference<List<String>>() {});
