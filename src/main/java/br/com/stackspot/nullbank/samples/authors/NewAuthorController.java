package br.com.stackspot.nullbank.samples.authors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
public class NewAuthorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewAuthorController.class);

    private final AuthorRespository respository;

    public NewAuthorController(AuthorRespository respository) {
        this.respository = respository;
    }

    @Operation(summary = "Create a new author")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Author created",
                    headers = @Header(
                            name = "Location",
                            description = "URI of the author created",
                            schema = @Schema(type = "string")
                    ),
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid parameters",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(ref = "https://opensource.zalando.com/restful-api-guidelines/models/problem-1.0.1.yaml#/Problem"),
                            examples = @ExampleObject(
                                    """
                                        {
                                           "status" : 400,
                                           "title" : "Constraint Violation",
                                           "type" : "https://zalando.github.io/problem/constraint-violation",
                                           "violations" : [{
                                             "field" : "birthdate",
                                             "message" : "must be a past date"
                                           }, {
                                             "field" : "email",
                                             "message" : "must be a well-formed email address"
                                           } ]
                                         }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Author is underage",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(ref = "https://opensource.zalando.com/restful-api-guidelines/models/problem-1.0.1.yaml#/Problem"),
                            examples = @ExampleObject(
                                    """
                                        {
                                          "status" : 422,
                                          "title" : "Unprocessable Entity",
                                          "detail" : "author is underage"
                                        }
                                    """
                            )
                    )
            )
    })
    @PostMapping("/api/v1/authors")
    public ResponseEntity<?> create(@RequestBody @Valid NewAuthorRequest request, UriComponentsBuilder uriBuidler) {

        // Tip: As you can see, we do NOT need any object mapper to convert the payload to an entity
        Author author = request.toModel();
        LOGGER.info(
            "A new author has been created = {}", author
        );

        // Tip: Execute your business logic here, right after validating and converting your
        // request's payload, like persisting an entity into database or sending an event to a broker
        respository.save(author);

        URI location = uriBuidler.path("/api/v1/authors/{id}")
                            .buildAndExpand(author.getId())
                            .toUri();

        return ResponseEntity
                .created(location)
                .build();
    }

}
