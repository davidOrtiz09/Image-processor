package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.ImagenesDAO;
import forms.FormData;
import models.Producto;
import play.data.Form;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;
import play.data.FormFactory;
import service.ProcesadorService;
import views.html.*;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletionStage;


public class Application extends Controller {

    private final HttpExecutionContext httpExecutionContext;
    private final FormFactory formFactory;
    private final ProcesadorService procesadorService;

    @Inject
    public Application(HttpExecutionContext httpExecutionContext, FormFactory formFactory, ProcesadorService procesadorService) {
        this.httpExecutionContext = httpExecutionContext;
        this.formFactory = formFactory;
        this.procesadorService = procesadorService;
    }


    public Result index() {
        return ok(views.html.index.render("Pagina inicio"));
    }
    public Result mostrarFormularioImagen() {
        return ok(views.html.uploadFileForm.apply());
    }

    public Result subirImagen() {
        Form<FormData> form = formFactory.form(FormData.class);
        return ok(views.html.image_upload.render(form));
    }

    public CompletionStage<Result> AnalizeImage() throws IOException {
        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> filePart = body.getFile("image");
        File file = filePart.getFile();
        return procesadorService.listarImagenesParecidas(file).thenApplyAsync(answer -> {
            ObjectNode arrayResult = Json.newObject();
            ArrayNode finalArray = arrayResult.putArray("productos_encontrados");
            for(Producto producto: answer){
                JsonNode result = Json.parse("{\"nombre\":" + "\"" + producto.nombre + "\"" +",\"precio\":" + "\"" + producto.precio.toString() + "\"" + ",\"url\":" + "\"" + producto.url + "\"" +"}");
                finalArray.add(result);
            }
            return ok(finalArray);
        }, httpExecutionContext.current());
    }
}