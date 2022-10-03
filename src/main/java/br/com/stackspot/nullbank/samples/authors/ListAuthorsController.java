package br.com.stackspot.nullbank.samples.authors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ListAuthorsController {

    private final AuthorRespository respository;

    public ListAuthorsController(AuthorRespository respository) {
        this.respository = respository;
    }

    @Operation(summary = "List all authors")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "List all found authors or an empty list",
                content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = AuthorResponse.class))
                )
        )
    })
    @GetMapping("/api/v1/authors")
    public ResponseEntity<?> listAll() {

        List<Author> authors = respository.findAll();

        return ResponseEntity.ok(
                authors.stream()
                    .map(AuthorResponse::new)
                    .toList()
        );
    }

}
