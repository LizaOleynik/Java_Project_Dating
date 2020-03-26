package ig_group;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

import static ig_group.Type_Relation.*;
import static ig_group.Type_Relation.Friends;
import static org.junit.Assert.assertNotNull;


public class Сommunity {
    private ArrayList<Person> people;
    private ArrayList<Relation> friendships;
    public Hobby root;
    static Hobby this_node;
    static int this_level;
    static int count_people = 0;
    static SimpleWeightedGraph<String, DefaultWeightedEdge> g;
    static SimpleWeightedGraph<String, DefaultWeightedEdge> g_p;
    static SimpleWeightedGraph<String, DefaultWeightedEdge> all;

    public static SimpleWeightedGraph<String, DefaultWeightedEdge> getTreeHobbyForDraw() {return  g;}
    public static SimpleWeightedGraph<String, DefaultWeightedEdge> getGraphPeopleForDraw() {return  g_p;}
    public static SimpleWeightedGraph<String, DefaultWeightedEdge> getGraphOfAll() {return  all;}

    public Сommunity() throws Exception {
        this.people = new ArrayList<>();
        this.friendships = new ArrayList<>();
        this.g = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        this.g_p = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        this.all = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        this.root = get_Root_Hobby();
        createHobbyGraph();
        createPersons(generate_People());
        createFriendships(generate_Relations());
    }

    private static void add_Node(String name_factor) throws Exception {
        int level = 0;
        for (int i =0; i < name_factor.length(); i++){
            if (name_factor.charAt(i) == '-')
                level++;
            else break;
        }
        String [] line = name_factor.split(" ");
        if (level == this_level + 1) {
            Hobby new_hobby = new Hobby(line[1], Integer.parseInt(line[2]), this_node);
            String name = new_hobby.getName();
            g.addVertex(name);
            g.addEdge(this_node.getName(), name);
            all.addVertex(name);
            all.addEdge(this_node.getName(), name);
            this_node.addChild(new_hobby);
            this_node = new_hobby;
            this_level = level;
        }
        else if(level == this_level){
            Hobby new_hobby = new Hobby(line[1], Integer.parseInt(line[2]), this_node.getParent());
            this_node.getParent().addChild(new_hobby);
            String name = new_hobby.getName();
            g.addVertex(name);
            g.addEdge(this_node.getParent().getName(), name);
            all.addVertex(name);
            all.addEdge(this_node.getParent().getName(), name);
            this_node = new_hobby;
        }
        else if(level < this_level) {
            this_node = this_node.getParent();
            this_level--;
            add_Node(name_factor);
        }
        else
            throw new Exception(String.format("invalid data %d %d", this_level, level ));
    }

    public static Hobby get_Root_Hobby() throws Exception {
        ArrayList<String> lines = read_From_File("src/hobby.txt");
        this_level = 0;
        String [] line = lines.get(0).split(" ");
        Hobby root = new Hobby(line[0],Integer.parseInt(line[1]));
        g.addVertex(root.getName());
        all.addVertex(root.getName());
        this_node = root;
        for (int i =1; i<lines.size(); i++){
            add_Node(lines.get(i));
        }
        return root;
    }

    public static ArrayList<String> read_From_File(String file_name){
        ArrayList<String> lines = new ArrayList<>();
        try {
            File file = new File(file_name);
            FileReader read = new FileReader(file);
            BufferedReader buf_read = new BufferedReader(read);
            String str = buf_read.readLine();
            while (str != null) {
                lines.add(str);
                str = buf_read.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static ArrayList<Map<String, String>> generate_People() {
        ArrayList<String> people_file = read_From_File("src/people.txt");
        ArrayList<Map<String, String>> people = new ArrayList<>();
        for (String line : people_file) {
            String[] str = line.split(" ");
            count_people++;
            Map<String, String> person = new HashMap<>();
            person.put("id", Integer.toString(count_people));
            person.put("name", str[0]);
            person.put("gender", str[1]);
            person.put("age", str[2]);
            people.add(person);
        }
        return people;
    }
    public static ArrayList<Map<String, Object>> generate_Relations(){
        ArrayList<String> relations_file = read_From_File("src/relations.txt");
        ArrayList<Map<String, Object>> relations = new ArrayList<>();
        for(String line: relations_file){
            String[] info = line.split(" ");
            Map<String, Object> friend = new HashMap<>();
            friend.put("id1", new Integer(info[0]));
            friend.put("id2", new Integer(info[1]));
            friend.put("type", Type_Relation.valueOf(info[2]));
            relations.add(friend);
        }

        return relations;
    }

    public void createPersons(ArrayList<Map<String, String>> persons){
        persons.forEach(p -> {
            Person person = new Person(new Integer(p.get("id")), (String)p.get("name"),
                    p.get("gender").equals("m") ? Gender.Man : Gender.Woman, new Integer(p.get("age")));
            this.people.add(person);
            g_p.addVertex(person.get_Name());
            all.addVertex(person.get_Name());
        });
    }

    public void createFriendships(ArrayList<Map<String, Object>> bonds){
        bonds.forEach(b -> {
            Relation r = null;
            try {
                r = new Relation(findPersonById((int)b.get("id1")), findPersonById((int)b.get("id2")), (Type_Relation)b.get("type"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.friendships.add(r);
            r.getP_1().addRelation(r);
            r.getP_2().addRelation(r);
            DefaultWeightedEdge e = g_p.addEdge(r.getP_1().get_Name(), r.getP_2().get_Name());
            g_p.setEdgeWeight(e, r.getIntType(r.getType()));
            DefaultWeightedEdge e_ = all.addEdge(r.getP_1().get_Name(), r.getP_2().get_Name());
            all.setEdgeWeight(e_, r.getIntType(r.getType()));
        });
    }

    private Person findPersonById(int id) throws Exception {
        for(int i = 0; i< this.people.size(); i++){
            if (this.people.get(i).id == id)
                return this.people.get(i);
        }
        throw new Exception("Несуществующий id");
    }

    private Person findPersonByName(String name) throws Exception {
        for(int i = 0; i< this.people.size(); i++){
            String n = this.people.get(i).get_Name();
            if (n.equals(name))
                return this.people.get(i);
        }
        throw new Exception("Несуществующее имя");
    }

    public void addPerson(String name, String gender, int age) throws Exception {
        count_people++;
        Person p = new Person(count_people, name, gender.equals("m") ? Gender.Man : Gender.Woman, age);
        this.people.add(p);
        g_p.addVertex(name);
        all.addVertex(name);
    }

    @Test
    public List<String> getMinPathBetweenPersons(Graph g, String p1, String p2) {
        DijkstraShortestPath dijkstraShortestPath
                = new DijkstraShortestPath(g);
        List<String> shortestPath = dijkstraShortestPath
                .getPath(p1,p2).getVertexList();
        assertNotNull(shortestPath);
        return shortestPath;
    }

    @Test
    public List<String> getMinPathBetweenPersonsWithConcreteHobby(String p, String h)  throws Exception {
        ArrayList<String> list_nodes = getNodesWithPeopleAround(h);
        List<String> min_path = null;
        for(int i = 0; i < list_nodes.size(); i++)
        {
            if(!list_nodes.get(i).equals(p))
            {
                List<String> l = getMinPathBetweenPersons(g_p, p, list_nodes.get(i));
                if(min_path == null || l.size() < min_path.size())
                    min_path = l;
            }
        }
        return min_path;
    }

    public Graph createHobbyGraph() {
        Graph<Hobby, Integer> g = new SimpleWeightedGraph<>(Integer.class);
        g.addVertex(this.root);
        this.root.addInGraph(g);
        return g;
    }

    public Hobby findHobbyByName(String name){
        return (Hobby)root.findByName(name);
    }

    public void addHobbyForPerson(String person_name, String hobby_name) throws Exception {
        Hobby h = findHobbyByName(hobby_name);
        findPersonByName(person_name).addHobby(h);
        all.addEdge(person_name, hobby_name);
    }

    public void addRelationForPerson(String person_name1, String person_name2, int relation) throws Exception {
        Relation r = new Relation(findPersonByName(person_name1), findPersonByName(person_name2), getRelationType(relation));
        findPersonByName(person_name1).addRelation(r);
        findPersonByName(person_name2).addRelation(r);
        this.friendships.add(r);
        g_p.addEdge(person_name1, person_name2);
        all.addEdge(person_name1, person_name2);
    }

    private ArrayList<String> getNodesWithPeopleAround(String n) throws Exception {
        Set<DefaultWeightedEdge> list = all.edgesOf(n);
        Iterator iter = list.iterator();
        String e = "";
        String[] info = null;
        ArrayList<String> list_nodes = new ArrayList<>();
        while (iter.hasNext()) {
            e = ((DefaultWeightedEdge)iter.next()).toString().replaceAll(" ", "").replace("(", "").replace(")", "");
            info = e.split(":");
            String node;
            if(info[0] == n)
                node = info[1];
            else node = info[0];
            if(!g.containsVertex(node))
                list_nodes.add(node);
        }
        return list_nodes;
    }

    public Type_Relation getRelationType(int t){
        switch (t){
            case 1: return Friends;
            case 2: return Colleagues;
            case 3: return Relatives;
            case 4: return Couple;
            case 5: return Married_couple;
        } return Friends;
    }

    public ArrayList<Person> getPeople(){
        return people;
    }

    public ArrayList<Hobby> getHobbyPerson(String p_n) throws Exception {
        return findPersonByName(p_n).getHobby();
    }

    public ArrayList<Relation> getRelationsPerson(String p_n) throws Exception {
        return findPersonByName(p_n).getRelations();
    }
}