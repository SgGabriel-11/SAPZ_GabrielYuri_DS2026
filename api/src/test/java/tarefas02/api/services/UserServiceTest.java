package tarefas02.api.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import tarefas02.api.models.User;
import tarefas02.api.repositories.UserRepository;
import tarefas02.api.services.exceptions.DataBindingViolationException;
import tarefas02.api.services.exceptions.ObjectNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "usuario.teste", "senha12345");
    }

    // --- TESTES DE CAMINHO FELIZ (HAPPY PATH) ---

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorIdComSucesso() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User resultado = userService.findById(1L);

        assertNotNull(resultado);
        assertEquals(user.getId(), resultado.getId());
        assertEquals("usuario.teste", resultado.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve criar um novo usuário e criptografar a senha")
    void deveCriarUsuarioComSucesso() {
        when(bCryptPasswordEncoder.encode("senha12345")).thenReturn("senhaCriptografada");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        User novoUsuario = new User(null, "usuario.teste", "senha12345");
        User criado = userService.create(novoUsuario);

        assertNotNull(criado);
        assertEquals(1L, criado.getId());
        assertEquals("senhaCriptografada", criado.getPassword());
        verify(bCryptPasswordEncoder, times(1)).encode("senha12345");
        verify(userRepository, times(1)).save(novoUsuario);
    }

    @Test
    @DisplayName("Deve atualizar a senha do usuário com sucesso")
    void deveAtualizarUsuarioComSucesso() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.encode("novaSenha123")).thenReturn("novaSenhaCriptografada");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User usuarioAtualizado = new User(1L, null, "novaSenha123");
        User resultado = userService.update(usuarioAtualizado);

        assertNotNull(resultado);
        verify(userRepository, times(1)).findById(1L);
        verify(bCryptPasswordEncoder, times(1)).encode("novaSenha123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve deletar usuário por ID com sucesso")
    void deveDeletarUsuarioComSucesso() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).deleteById(1L);
        verify(userRepository, times(1)).flush();
    }

    // --- TESTES DE CENÁRIOS DE EXCEÇÃO CUSTOMIZADA ---

    @Test
    @DisplayName("Deve lançar ObjectNotFoundException quando o ID não existir")
    void deveLancarObjectNotFoundExceptionQuandoUsuarioNaoEncontrado() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            userService.findById(99L);
        });

        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve lançar DataBindingViolationException ao falhar exclusão por entidades relacionadas")
    void deveLancarDataBindingViolationExceptionAoDeletarComVinculos() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("Integrity violation")).when(userRepository).deleteById(1L);

        assertThrows(DataBindingViolationException.class, () -> {
            userService.delete(1L);
        });

        verify(userRepository, times(1)).deleteById(1L);
    }
}
