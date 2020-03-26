package ig_group;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static ig_group.Сommunity.*;
import static org.junit.Assert.assertTrue;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
        Сommunity net = new Сommunity();
        Scanner in = new Scanner(System.in);
        while (true)
        {
            System.out.println("\n1 - добавить человека;\n" +
                    "2 - добавить хобби для выбранного человека;\n" +
                    "3 - вывести список людей;\n" +
                    "4 - вывести хобби выбранного человека;\n" +
                    "5 - вывести отношения выбранного человека;\n" +
                    "6 - добавить отношение для выбранных людей \n" +
                    "7 - кратчайший путь от одного человека до другого;\n" +
                    "8 - кратчайший путь до человека с определённым хобби;\n" +
                    "9 - построить графы;\n");
        functions(in.nextInt(), net);
        }
        /*Hobby math = net.findByName("math");
        Hobby football = net.findByName("football");
        double x = net.minInterestPath(math, football, g);
        System.out.println(x);*/
    }

    public static void functions(int i, Сommunity c) throws Exception {
        Scanner in = new Scanner(System.in);
        switch(i){
            case 1: {
                System.out.print("Введите имя: ");
                String n = in.nextLine();
                System.out.print("Введите пол (m - мужчина, w - женщина): ");
                String g = in.nextLine();
                System.out.print("Введите возраст: ");
                int a = in.nextInt();
                c.addPerson(n, g, a);
                break;
            }
            case 2: {
                System.out.print("Введите имя человека: ");
                String p = in.nextLine();
                System.out.print("Введите хобби: ");
                String h = in.nextLine();
                c.addHobbyForPerson(p, h);
                break;
            }
            case 3: {
                for(int j=0; j < c.getPeople().size(); j++)
                {
                    Person p = c.getPeople().get(j);
                    System.out.printf("%s, Пол: %s, Возраст: %s\n", p.get_Name(), p.getGender(), String.valueOf(p.getAge()));
                }
                break;
            }
            case 4: {
                System.out.print("Введите имя человека: ");
                String p = in.nextLine();
                ArrayList<Hobby> arr = c.getHobbyPerson(p);
                for(int j=0; j < arr.size(); j++)
                {
                    System.out.printf("%s,\n", arr.get(j).getName());
                }
                break;
            }
            case 5: {
                System.out.print("Введите имя человека: ");
                String p = in.nextLine();
                ArrayList<Relation> arr = c.getRelationsPerson(p);
                for(int j=0; j < arr.size(); j++)
                {
                    Person partner = arr.get(j).Partner(p);
                    System.out.printf("%s (%s),\n", partner.get_Name(), arr.get(j).getType());
                }
                break;
            }
            case 6: {
                System.out.print("Введите имя первого человека: ");
                String p1 = in.nextLine();
                System.out.print("Введите имя второго человека: ");
                String p2 = in.nextLine();
                System.out.print("Введите тип отношений (1 - дружба, 2 - коллеги, 3 - родственники, 4 - пара, 5 - супруги): ");
                int t = in.nextInt();
                c.addRelationForPerson(p1, p2, t);
                break;
            }
            case 7: {
                System.out.print("Введите имя первого человека: ");
                String p1 = in.nextLine();
                System.out.print("Введите имя второго человека: ");
                String p2 = in.nextLine();
                List<String> list = c.getMinPathBetweenPersons(c.getGraphPeopleForDraw(), p1, p2);
                for(int j=0; j < list.size(); j++)
                {
                    System.out.printf("%s ", list.get(j));
                }
                break;
            }
            case 8: {
                System.out.print("Введите имя человека: ");
                String p = in.nextLine();
                System.out.print("Введите хобби: ");
                String h = in.nextLine();
                List<String> list = c.getMinPathBetweenPersonsWithConcreteHobby(p, h);
                for(int j=0; j < list.size(); j++)
                {
                    System.out.printf("%s ", list.get(j));
                }
                break;
            }
            case 9: {
                givenAdaptedGraph__whenWriteBufferedImage__thenFileShouldExist(getTreeHobbyForDraw(), "src/graph_hobby.png");
                givenAdaptedGraph__whenWriteBufferedImage__thenFileShouldExist(getGraphPeopleForDraw(), "src/graph_people.png");
                givenAdaptedGraph__whenWriteBufferedImage__thenFileShouldExist(getGraphOfAll(), "src/graph_all.png");
                break;
            }
        }
    }

    @Test
    public static void givenAdaptedGraph__whenWriteBufferedImage__thenFileShouldExist(SimpleWeightedGraph<String, DefaultWeightedEdge> g, String path) throws IOException {

        JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter =
                new JGraphXAdapter<String, DefaultWeightedEdge>(g);
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image =
                mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File(path);
        ImageIO.write(image, "PNG", imgFile);

        assertTrue(imgFile.exists());
    }

}
