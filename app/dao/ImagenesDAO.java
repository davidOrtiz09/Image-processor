package dao;


import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import models.Producto;
import org.bson.Document;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;

import static com.mongodb.client.model.Filters.eq;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class ImagenesDAO {

   private MongoCollection<Document> collection;

    @Inject
    public ImagenesDAO(){
        this.collection = DBClient.getConnection().getCollection("productos");
    }

    public CompletionStage<ArrayList<Producto>> getProductosByTag(String tag) {
        FindIterable<Document> documents = collection.find(eq("tags", tag));
        ArrayList<Producto> productos = new ArrayList<>();
        for(Document document : documents){
            String url = document.getString("imagen_url");
            String nombre = document.getString("nombre");
            Double precio = document.getDouble("precio");
            productos.add(new Producto(nombre, precio, url));
        }


        return completedFuture(productos);
    }

}
