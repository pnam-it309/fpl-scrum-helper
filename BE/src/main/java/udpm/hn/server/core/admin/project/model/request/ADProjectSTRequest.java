package udpm.hn.server.core.admin.project.model.request;


import jakarta.persistence.NamedAttributeNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class ADProjectSTRequest {

    private String name;

    private String code;

    private String email;

    private String image;

    private String role;

}
