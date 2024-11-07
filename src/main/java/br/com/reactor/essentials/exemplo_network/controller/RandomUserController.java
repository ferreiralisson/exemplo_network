package br.com.reactor.essentials.exemplo_network.controller;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RandomUserController {

    private final RandomUserService randomUserService;

    public RandomUserController(RandomUserService randomUserService) {
        this.randomUserService = randomUserService;
    }

    @GetMapping("/user")
    public RandomUserResponse getRandomUser() {
        return randomUserService.fetchAndSaveUser();
    }
}

@Service
class RandomUserService {

    private final NameRepository nameRepository;

    RandomUserResponse randomUserResponse = new RandomUserResponse();

    RandomUserService(NameRepository nameRepository) {
        this.nameRepository = nameRepository;
    }

    public RandomUserResponse fetchAndSaveUser() {
        RestTemplate restTemplate = new RestTemplate();
        RandomUserResponse response = restTemplate.getForObject("https://randomuser.me/api", RandomUserResponse.class);

        if (response != null && !response.getResults().isEmpty()) {
            RandomUserResponse.Result result = response.getResults().get(0);
            randomUserResponse = response;

            Name name = Name.builder()
                    .title(result.getName().getTitle())
                    .first(result.getName().getFirst())
                    .last(result.getName().getLast())
                    .build();

            nameRepository.save(name);
        }

        return randomUserResponse;
    }

}


@Data
class RandomUserResponse {
    private List<Result> results;
    private Info info;

    @Data
    public static class Result {
        private String gender;
        private Name name;
        private Location location;
        private String email;
        private Login login;
        private Dob dob;
        private Registered registered;
        private String phone;
        private String cell;
        private Id id;
        private Picture picture;
        private String nat;
    }

    @Data
    public static class Name {
        private String title;
        private String first;
        private String last;
    }

    @Data
    public static class Location {
        private Street street;
        private String city;
        private String state;
        private String country;
        private Object postcode;
        private Coordinates coordinates;
        private Timezone timezone;

        @Data
        public static class Street {
            private int number;
            private String name;
        }

        @Data
        public static class Coordinates {
            private String latitude;
            private String longitude;
        }

        @Data
        public static class Timezone {
            private String offset;
            private String description;
        }
    }

    @Data
    public static class Login {
        private String uuid;
        private String username;
        private String password;
        private String salt;
        private String md5;
        private String sha1;
        private String sha256;
    }

    @Data
    public static class Dob {
        private String date;
        private int age;
    }

    @Data
    public static class Registered {
        private String date;
        private int age;
    }

    @Data
    public static class Id {
        private String name;
        private String value;
    }

    @Data
    public static class Picture {
        private String large;
        private String medium;
        private String thumbnail;
    }

    @Data
    public static class Info {
        private String seed;
        private int results;
        private int page;
        private String version;
    }
}


@Getter
@Setter
@Builder
@AllArgsConstructor
@Document(collection = "name")
class Name {
    private String title;
    private String first;
    private String last;
}

@Repository
interface NameRepository extends MongoRepository<Name, String> {
}