package ig_group;

import java.util.ArrayList;

enum Gender {
    Man,
    Woman
}

public class Person {
    public boolean visitFlg = false;
    public int id;
    private String name;
    private int age;
    private Gender gender;
    private ArrayList<Hobby> hobbies;
    private ArrayList<Relation> relations;

    public Person(int id, String name, Gender gender, int age){
        this(name, gender,age);
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public Person(String name, Gender gender, int age){
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.hobbies = new ArrayList<>();
        this.relations = new ArrayList<>();
    }

    public String get_Name() { return this.name; }
    public String getGender() { return this.gender.name(); }
    public int getAge() { return this.age; }
    public ArrayList<Hobby> getHobby() {return this.hobbies; }
    public ArrayList<Relation> getRelations() {return this.relations; }

    public void addAllFriends(ArrayList<Person> friends){
        this.relations.forEach(f-> {
            Person friend = f.Partner(this);
            if (! friend.visitFlg) {
                friend.visitFlg = true;
                friends.add(friend);
                friend.addAllFriends(friends);
            }
        });
    }

    public void addHobby(Hobby hobby){
        if(!hobbies.contains(hobby))
            hobbies.add(hobby);
    }

    public void addRelation(Relation r){
        if(!relations.contains(r))
            relations.add(r);
    }
}