import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Post;
import facebook4j.ResponseList;

public class Main {
	static final Logger logger = LogManager.getLogger(Main.class);
	private static final String CONFIG_DIR = "config";
	private static final String CONFIG_FILE = "fbcmd4j.properties";

	public static void saveToFile(String fileName, ResponseList<Post> posts, Scanner scanner) {
		System.out.println("Quieres guardarlo en un archivo?");
		String userParam= scanner.nextLine();
		if (userParam.contains("Si") || userParam.contains("si")) {
			List<Post> post = new ArrayList<>();
			int num = 0;
			while(num <= 0) {
				try {
					System.out.println("Cuantos post quieres guardar?");
					num = Integer.parseInt(scanner.nextLine());					
					if(num <= 0) {
						System.out.println("Numero invalido");
					}else {
						for(int i = 0; i<num; i++){
							if(i>posts.size()-1) break;
							post.add(posts.get(i));
						}
					}
				} 
				catch(NumberFormatException e) {
					logger.error(e);
				}
			}
			Utils.savePosts(fileName, post);
		}
	}
	
	public static void main(String[] args) {
		logger.info("Iniciando app");
		Facebook fb =  null;
		Properties props = null;
		try {
			props = Utils.loadConfigFile(CONFIG_DIR, CONFIG_FILE);
		} catch (IOException ex) {
			logger.error(ex);
		}
		
		try {
			Scanner s = new Scanner(System.in);
			while(true) {
				fb = Utils.configFacebook(props);
				System.out.println("Cliente de Facebook\n");
				System.out.println("Opciones:");
				System.out.println("(1) = NewsFeed");
				System.out.println("(2) = Wall");
				System.out.println("(3) = Publicar Estado");
				System.out.println("(4) = Publicar Link");
				System.out.println("(5) = Salir \n");
				System.out.println("Ingresa una opcion: ");
				try {
					int userParam= s.nextInt();
					s.nextLine();
					switch (userParam) {
					case 1:
						System.out.println("Cargando Newsfeed");
						ResponseList<Post> newsFeed = fb.getFeed();
						for (Post p : newsFeed) {
							Utils.printPost(p);
						}
						saveToFile("NewsFeed", newsFeed,s);
						break;
					case 2:
						System.out.println("Cargando Wall");
						ResponseList<Post> wall = fb.getPosts();
						for (Post p : wall) {
							Utils.printPost(p);
						}		
						saveToFile("Wall", wall, s);
						break;
					case 3:
						System.out.println("Ingresa tu estado: ");
						String estado = s.nextLine();
						Utils.postStatus(estado, fb);
						break;
					case 4:
						System.out.println("Ingresa el link: ");
						String link = s.nextLine();
						Utils.postLink(link, fb);
						break;
					case 5:
						System.out.println("Terminando programa");
						System.exit(0);
						break;
					default:
						logger.error("Opción inválida");
						break;
					}
				} catch (InputMismatchException ex) {
					System.out.println("Ocurrió un errror, favor de revisar log.");
					logger.error("Opción inválida. %s. \n", ex.getClass());
				} catch (FacebookException ex) {
					System.out.println("Ocurrió un errror, favor de revisar log.");
					logger.error(ex.getErrorMessage());
				} catch (Exception ex) {
					System.out.println("Ocurrió un errror, favor de revisar log.");
					logger.error(ex);	
				}
				System.out.println();
			}
		} catch (Exception ex) {
			logger.error(ex);
		}
	}
}
