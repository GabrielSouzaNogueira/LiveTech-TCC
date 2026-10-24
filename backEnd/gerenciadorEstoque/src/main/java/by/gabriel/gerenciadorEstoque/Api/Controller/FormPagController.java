package by.gabriel.gerenciadorEstoque.Api.Controller;

import by.gabriel.gerenciadorEstoque.Api.DTO.FormPagDTO.Consultas.SelectFormPagStatusDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.FormPagDTO.FormPagDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.FormPagDTO.UpdateFormPagDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.Response.ResponseDTO;
import by.gabriel.gerenciadorEstoque.Model.FormaPag.FormaPagto;
import by.gabriel.gerenciadorEstoque.Services.FormPagService;
import org.apache.juli.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/formPag")
public class FormPagController {

    private final FormPagService formPagService;

    public FormPagController(FormPagService formPagService){
        this.formPagService = formPagService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<SelectFormPagStatusDTO>> listar() {

        List<SelectFormPagStatusDTO> listados =  formPagService.listar();

        return ResponseEntity.ok(listados);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<ResponseDTO> cadastro(@RequestBody FormPagDTO dto, @RequestHeader("X-Usuario-Logado") String userLogado) {

        FormaPagto formaPagto = formPagService.cadastro(dto,userLogado);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO(
                true,
                "Forma de pagamento cadastrado com sucesso!",
                Instant.now().toString())
        );

    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<ResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody UpdateFormPagDTO dto,
            @RequestHeader("X-Usuario-Logado") String userLogado) {

        Boolean formaPagto = formPagService.atualizar(id, dto, userLogado);

        if(!formaPagto) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(
                    false,
                    "Não foi possível atualizar a forma de pagamento!",
                    Instant.now().toString())
            );
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(
                true,
                "Forma de pagamento atualizada com sucesso!",
                Instant.now().toString())
        );
    }

    @PatchMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO> delecao(@PathVariable Long id, @RequestHeader("X-Usuario-Logado") String userLogado) {

        Boolean deletado = formPagService.delecao(id, userLogado);

        if (!deletado) {

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(
                    false,
                    "Não foi possivel desativada a Forma de pagamento",
                    Instant.now().toString()
            ));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(
                true,
                "Forma de pagamento desativada com sucesso!",
                Instant.now().toString()
        ));
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<FormaPagto> buscarPorId(@PathVariable Long id) {

        FormaPagto formaPagto = formPagService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK).body(formaPagto);
    }

}
