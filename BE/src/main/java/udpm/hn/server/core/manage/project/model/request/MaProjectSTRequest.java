package udpm.hn.server.core.manage.project.model.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class MaProjectSTRequest {

    private String name;

    private String code;

    private String email;

    private String image;

    private String role;

}
