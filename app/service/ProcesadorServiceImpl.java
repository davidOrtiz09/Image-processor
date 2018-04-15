package service;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import dao.ImagenesDAO;
import models.Producto;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionStage;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class ProcesadorServiceImpl implements ProcesadorService {

    private ImageAnnotatorClient vision;
    private  ImagenesDAO imagenesDAO;

    @Inject
    public ProcesadorServiceImpl(ImagenesDAO imagenesDAO) {
        this.imagenesDAO = imagenesDAO;
        try{
            FileInputStream credentialsStream = new FileInputStream("google_credentials/arquisoft-201810-9106d813d2b9.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
            FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);
            ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                    .setCredentialsProvider(credentialsProvider)
                    .build();
            this.vision = ImageAnnotatorClient.create(settings);
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }

    public CompletionStage<ArrayList<Producto>> listarImagenesParecidas(File file) throws IOException {
        return connectGoogleVision(file).thenComposeAsync(answers -> {
            ArrayList<String> descripciones = new ArrayList<>();
            for(EntityAnnotation entity: answers) {
                descripciones.add(entity.getDescription());
            }
            if(descripciones.isEmpty()){
                return completedFuture(new ArrayList<>());
            } else {
                return imagenesDAO.getProductosByTag(descripciones.get(0));
            }
        });
    }

    public CompletionStage<List<EntityAnnotation>> connectGoogleVision(File file) throws IOException {
        byte[] data = Files.readAllBytes(file.toPath());
        ByteString imgBytes = ByteString.copyFrom(data);

        List<AnnotateImageRequest> requests = new ArrayList<>();
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();
        requests.add(request);

        BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
        List<AnnotateImageResponse> responses = response.getResponsesList();

        for (AnnotateImageResponse res : responses) {
            if (!res.hasError()) {
                return completedFuture(res.getLabelAnnotationsList());
            }
        }
        return completedFuture(Collections.emptyList());
    }
}
