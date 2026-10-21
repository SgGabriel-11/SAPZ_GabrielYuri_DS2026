package tarefas02.api.controllers; // Declaração do pacote onde a classe controller está localizada

import tarefas02.api.services.UserService;
import tarefas02.api.repositories.TaskRepository;
import java.net.URI; // Importa a classe URI para construir e manipular HTTP de novos recursos
import java.util.List; // Importa a interface  List para manipular onde a classe controller está localizada 

import org.springframework.beans.factory.annotation.Autowired; // injção automatica do Spring
import org.springframework.http.ResponseEntity; // importa a classe para montar a resposta HTTP completa(status, headers, corpo)
import org.springframework.validation.annotation.Validated;// importa anotação para habiltar suporte a validação no controller
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;// mapeia requisições do tipo delete
import org.springframework.web.bind.annotation.GetMapping;// mapeia requisições do tipo GET
import org.springframework.web.bind.annotation.PathVariable;// mapeia variaveis passadas diretamente via caminho da URL
import org.springframework.web.bind.annotation.PostMapping;// mapeia requisições do tipo POST
import org.springframework.web.bind.annotation.PutMapping;// mapeia requisições do tipo PUT
import org.springframework.web.bind.annotation.RequestBody;// converste objetos JSON em ojetos JAVA
import org.springframework.web.bind.annotation.RequestMapping;// importa anotação para definir o caminho/rota bas do controlador
import org.springframework.web.bind.annotation.RestController; //  importa anotação que define esta classe como um controller REST
import org.springframework.web.servlet.support.ServletUriComponentsBuilder; // Importa utilitario para gerar a URI da requisição atual dinamicamente.


import jakarta.validation.Valid; // importa a anotação para acionar a validação do corpo da requisição
import tarefas02.api.models.Task; // importa a entidade Task do pacote de modelos do projeto
import tarefas02.api.services.TaskService; // Importa a caçassse de serviço TaskService do projeto

@RestController  //Define a classeo com um controlador REST que retorna respostas em JSON
@RequestMapping ("/task") // Define /task como a rota base de todos do endpoints deste controlador
@Validated  // Habilita o suporte as validaçções dentro do controlador
@CrossOrigin ("*")
public class TaskController { // Declaração de Classe píbulica TaskController

    @Autowired 
    private TaskService taskService;

    @Autowired
    private UserService userService;

   

    @GetMapping("/{id}") //Mapeia requisições HTTP GET na rota "/task/{id}"
    public ResponseEntity<Task> findById(@PathVariable Long id){ //Busca tarefa especifica pelo seu ID
        Task obj = this.taskService.findById(id); //Chama  a camada de de serviço para buscar a tarefa pelo seu ID
        return ResponseEntity.ok().body(obj); // Retorna HTTp 200(ok) 
    } // Fim do método findById

  //  @GetMapping("/user/{userid}")
  //  public ResponseEntity<List<Task>> findAllByUserId(@PathVariable Long userId){
   //     List<Task> objs = this.taskService.findAllByUserId(userId);
   //     return ResponseEntity.ok().body(objs);
  //  }

    @GetMapping("/user/{userId}")
public ResponseEntity<List<Task>> findAllByUserId(@PathVariable("userId") Long userId) {
    this.userService.findById(userId);
    List<Task> objs = this.taskService.findAllByUserId(userId);
    return ResponseEntity.ok().body(objs);
}


    @PostMapping 
    public ResponseEntity<Void> create(@Valid @RequestBody Task obj){
        this.taskService.create(obj);
        URI url = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(url).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody Task obj, @PathVariable Long id){
        obj.setId(id);
        this.taskService.update(obj);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        this.taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

  }//fim da classe TaskController