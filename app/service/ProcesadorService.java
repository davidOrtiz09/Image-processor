package service;

import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.inject.ImplementedBy;
import models.Producto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

@ImplementedBy(ProcesadorServiceImpl.class)
public interface ProcesadorService {

    public CompletionStage<List<EntityAnnotation>> connectGoogleVision(File file) throws IOException;

    public CompletionStage<ArrayList<Producto>> listarImagenesParecidas(File file) throws IOException;

}
