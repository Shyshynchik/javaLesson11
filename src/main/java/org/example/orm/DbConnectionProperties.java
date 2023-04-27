package org.example.orm;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
class DbConnectionProperties {

    private String url;
    private String user;
    private String password;

}
