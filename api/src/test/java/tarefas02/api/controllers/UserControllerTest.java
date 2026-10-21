package tarefas02.api.controllers;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tarefas02.api.models.User;
import tarefas02.api.services.UserService;
import tarefas02.api.services.exceptions.ObjectNotFoundException;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("POST /user - Deve criar usuario e retornar 201 com header Location")
    void deveCriarUsuarioERetornarStatus201() throws Exception {
        String jsonPayload = "{ \"username\": \"novo.usuario\", \"password\": \"senhaForte123\" }";

        when(userService.create(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(10L);
            return u;
        });

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/user/10")));
    }

    @Test
    @DisplayName("GET /user/{id} - Deve retornar usuario por ID com status 200 e payload JSON")
    void deveBuscarUsuarioPorIdERetornarStatus200() throws Exception {
        User user = spy(new User(1L, "gabriel.silva", "senha12345"));
        doReturn(Set.of()).when(user).getProfiles();
        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/user/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("gabriel.silva"));
    }

    @Test
    @DisplayName("GET /user/{id} - Deve retornar status 404 quando o usuario nao for encontrado")
    void deveRetornarStatus404QuandoUsuarioNaoExistir() throws Exception {
        when(userService.findById(99L)).thenThrow(new ObjectNotFoundException("Usuario nao encontrado! Id: 99"));

        mockMvc.perform(get("/user/99")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /user/{id} - Deve deletar usuario e retornar status 204 No Content")
    void deveDeletarUsuarioERetornarStatus204() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isNoContent());
    }
}