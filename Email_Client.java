// your index number: 200734G

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


//import libraries

public class Email_Client {

    static int recipientCount = 0;
    static BirthdayList birthdayList = BirthdayList.getBirthdayList();
    // hashmap to keep all the recepiant
    // object , hash key is from the email
    static Hashtable<String, Recipients> recipientObjects = new Hashtable<>(); 
                                                                          
    static GetLocalDate date = GetLocalDate.date();
    static SendMails sendMails = new SendGmailsAdupter();
    static FileManeger file = new FileManeger();
    static String[] dayList = date.getDate();
    static String today = dayList[0] + "/" + dayList[1] + "/" + dayList[2];
    

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Press Enter to start Email Client");
        restoreRecipients();
        
        scanner.nextLine();

        boolean start = true;

        while (start) {
            PersonalRecipients pr=new PersonalRecipients(today, today, today, today);
            pprintRecipient (pr);

            // send bd wishes for all the recipients that are added to the email client so
            // far that have bd today
            // and if bd wishes are send today bd wishes will not send every time the email
            // client opened
            BDWishes bdWishes=new BDWishes(today);
            bdWishes.sendWish();
            System.out.println("\nEnter option type: \n"
                    + "1 - Adding a new recipient\n"
                    + "2 - Sending an email\n"
                    + "3 - Printing out all the recipients who have birthdays\n"
                    + "4 - Printing out details of all the emails sent\n"
                    + "5 - Printing out the number of recipient objects in the application\n"
                    + "6 - Close the Email Client");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    // input format - Official: nimal,nimal@gmail.com,ceo
                    // Use a single input to get all the details of a recipient
                    // code to add a new recipient
                    // store details in clientList.txt file
                    // Hint: use methods for reading and writing files

                    /*
                     * input format :
                     * Official: <name>, <email>,<designation>
                     * Office_friend: <name>, <email>,<designation>,<Birthday>
                     * Personal: <name>,<nick-name>,<email>,<Birthday>
                     */
                    scanner.nextLine();
                    System.out.print("\nEnter the details of the recipient : ");
                    String input = scanner.nextLine();
                    //using a try catch block here so if any invalid recipent details was entered it wont be added to the text file
                    try {
                        String[] inputArr = input.split("[,]", 0);
    
                        // check to see if the recipient is already added to the table
                        if (recipientObjects.containsKey(inputArr[1]) || recipientObjects.containsKey(inputArr[2])) { 
                            System.out.println("\nRecipient has already added\n");
                            break;
                        }
                        addRecipientObj(input); // add a new reciption
                    } catch (Exception e) {
                        System.out.println("Invalid Recipient input");
                        break;
                    }
                    file.saveToTextFile(input,"clientList");

                    break;
                case 2:
                    // input format - email, subject, content
                    // code to send an email
                    System.out.print("\nEnter details (email, subject, content): ");
                    scanner.nextLine();
                    String[] emailDetails = (scanner.nextLine()).split("[,]", 3);
                    if (recipientObjects.containsKey(emailDetails[0])) {
                        Recipients recipient = recipientObjects.get(emailDetails[0]);
                        Email email = new Email(recipient, emailDetails[1], emailDetails[2]);
                        sendMails.SendMail(email);
                    } else {
                        System.out.println("\nResipient is not added yet or an Invalid input");
                    }
                    break;
                case 3:
                    // input format - yyyy/MM/dd (ex: 2018/09/17)
                    // code to print recipients who have birthdays on the given date 
                    String dateEntered;
                    System.out.print("\nEnter the date (yyyy/MM/dd): ");
                    scanner.nextLine();
                    dateEntered = scanner.nextLine();
                    try{
                    ArrayList<Recipients> recipients = birthdayList.getEmailsOfThisBD(dateEntered);
                    for (Recipients recip : recipients) {
                        System.out.println(recip.getEmailAddress()+"\n"+recip.getName()+"\n");
                    }
                    }catch(Exception e){
                    System.out.println("Invalid Date input");
                    }
                    break;
                case 4:
                    // input format - yyyy/MM/dd (ex: 2018/09/17)
                    // code to print the details of all the emails sent on the input date
                    System.out.print("Enter the date: ");
                    scanner.nextLine();
                    String date = scanner.nextLine();
                    try{
                    readEmails(date);
                    }catch(Exception e){
                        System.out.println("Invalid Date input");
                    }
                    break;
                case 5:
                    // code to print the number of recipient objects in the application
                    System.out.println("Number of recipients are : " + recipientCount);
                    break;
                case 6:
                    start = false;
                    break;

            }

            // start email client
            // code to create objects for each recipient in clientList.txt incriment count
            // when do this
            // use necessary variables, methods and classes
        }

        scanner.close();

    }
    public static void pprintRecipient(Recipients rp) {
        System.out.println(rp.getEmailAddress()+"\n"+rp.getName()+"\n");
    }

    /*
     * input format :
     * Official: <name>, <email>,<designation>
     * Office_friend: <name>, <email>,<designation>,<Birthday>
     * Personal: <name>,<nick-name>,<email>,<Birthday>
     */
    private static void addRecipientObj(String input) {
        String[] recipientInput = input.split("[ ]", 2);// getting the input and splitting it from the 1st space
        String[] recipienDetails = recipientInput[1].split("[,]", 0);// split the rest of the input from the comma
        String recipienType = recipientInput[0];
        String[] recipien = new String[recipienDetails.length + 1]; // making a array that the first index to be the
        // String todayWithoutYear=today.split("[/]", 2)[1]; //use to check Birth days                                                             // type and rest to be other details
        recipien[0] = recipienType;
        for (int i = 0; i < recipienDetails.length; i++) {
            recipien[i + 1] = recipienDetails[i];
        }
        recipientCount++; // increament recipien count

        // creat a correct type of object
        switch (recipien[0]) {
            case "Official:": // if the recipient type if official
                OfficeRecipients officeRecipient = new OfficeRecipients(recipien[1], recipien[2], recipien[3]);
                recipientObjects.put(recipien[2], officeRecipient); // store it in the hash map
                break;

            case "Office_friend:":// if the recipient type if Office_friend
                OfficeFriendRecipients officeFriendRecipient = new OfficeFriendRecipients(recipien[1], recipien[2],
                        recipien[3], recipien[4]);// make an Office_friend recipiant obj
                birthdayList.add(officeFriendRecipient);// add the recipient obj to the birth day list
                recipientObjects.put(recipien[2], officeFriendRecipient);// store it in the hash map

                break;

            case "Personal:":
                PersonalRecipients personaResipient = new PersonalRecipients(recipien[1], recipien[2], recipien[3],
                        recipien[4]);// make an persanol recipiant obj
                birthdayList.add(personaResipient);// add the recipient obj to the birth day list
                recipientObjects.put(recipien[3], personaResipient);// store it in the hash map

                break;
        }
    }

    private static void restoreRecipients() {
        ArrayList<String> recipientArray = file.readFromTextFile("clientList");
        if (recipientArray==null) {
            return;
        }
        for (String recipient : recipientArray) {
            addRecipientObj(recipient);
        }
    }
    //get the de serialized Array list of emails saved for this date and print details of each email
    private static void readEmails(String date){
        String[] dayArr= date.split("[/]",0);
        String fileNameDate=dayArr[0]+" "+dayArr[1]+" "+dayArr[2];
        ArrayList<Object> emails = (ArrayList<Object>)file.deSeriallizeThis(".\\Emails\\"+fileNameDate);
        for (Object email : emails) {
            Email email1 = (Email) email;
            System.out.println("\nSubject : "+email1.getSubject());
            System.out.println("Recipien Name : "+email1.getRecipient().getName());
            System.out.println("Recipien Email : "+email1.getRecipient().getEmailAddress()+"\n"); 
        }
    }

}

abstract class Recipients implements Serializable{
    protected String name=new String();
    protected String emailAddress=new String();
    
    public String getName() {
        return name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}

class PersonalRecipients extends Recipients{
    private String nickName=new String();
    private String birthDay = new String();

    public PersonalRecipients(String name, String nickName,String emailAddress, String birthDay) {
        this.nickName = nickName;
        this.birthDay = birthDay;
        this.name=name;
        this.birthDay=birthDay;
        this.emailAddress=emailAddress;
        //System.out.println("new persanol friend recipion added: "+name+" "+emailAddress+" "+nickName+" "+birthDay);
    }

    public String getBirthDay() {
        return birthDay;
    }

    public String getNickName() {
        return nickName;
    }
    
}

class OfficeRecipients extends Recipients{
    private String designation = new String();

    public OfficeRecipients(String name,String emailAddress,String designation) {
        this.designation = designation;
        this.name=name;
        this.emailAddress=emailAddress;
        //System.out.println("new office recipion added: "+name+" "+emailAddress+" "+designation);
    }

    public String getDesignation() {
        return designation;
    }
}


class OfficeFriendRecipients extends OfficeRecipients {
    private String birthDay = new String();

    public OfficeFriendRecipients(String name, String emailAddress, String designation,String birthDay) {
        super(name, emailAddress, designation);
        this.birthDay=birthDay;
        //System.out.println("new office friend recipion added: "+name+" "+emailAddress+" "+designation+" "+birthDay);
    }

    public String getBirthDay() {
        return birthDay;
    }
}


class Email implements Serializable{
    private Recipients recipient;
    private String subject;
    private String content;
    public Email(Recipients recipient, String subject, String content) {
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
    }
    public Recipients getRecipient() {
        return recipient;
    }
    public String getSubject() {
        return subject;
    }
    public String getContent() {
        return content;
    }
}


abstract class SendMails {
    protected Email email;
    
    private FileManeger fileManeger=new FileManeger();
    public abstract void SendMail(Email email);
    public void saveMail() {
        GetLocalDate today=GetLocalDate.date();
        String[] dateArr=today.getDate();
        String fileName= dateArr[0]+" "+dateArr[1]+" "+dateArr[2];
        fileManeger.seriallizeThis(email, fileName,".\\Emails\\",true);
        
    }
}

class SendGmailsAdupter extends SendMails{

    //created this gmail adupter class implementing the sendmail clazbecoz  now if we want 
    //to add a new email service like yahoo mail or umo mail insted of gmail we can easily do that by just introdusing 
    //a nmew adupter to that mail service class without changing the main email client code

    private String myEmailAdress="gwikumjith@gmail.com";
    private String myPassword="rcnujmhywqrwhtfx";
    SendGmail gmailSender;

    @Override
    public void SendMail(Email email) {
        super.email=email;
        saveMail();
        gmailSender=new SendGmail(myEmailAdress, myPassword, email.getRecipient().emailAddress, email.getSubject(), email.getContent());
        try {
            gmailSender.send();
        } catch (Exception e) {
            System.out.println("Error occured in Gmail sender");
            e.printStackTrace();
        }
    }

}


class SendGmail {
    protected  String myEmailAdress;
    protected  String myPassword;
    protected  String reciEmail;
    protected  String subject;
    protected  String TextBody;



    public SendGmail(String myEmailAdress, String myPassword, String reciEmail, String subject, String TextBody) {
        this.myEmailAdress = myEmailAdress;
        this.myPassword = myPassword;
        this.reciEmail = reciEmail;
        this.subject = subject;
        this.TextBody = TextBody;
        System.out.println("sending "+subject+" Gmail from "+this.myEmailAdress+" to "+reciEmail );
    }

    public void name() {
        
    } void send() throws Exception {

        System.out.println("Preparing email !");

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        // prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");

        String myEmail = myEmailAdress;
        String password = myPassword;

        Session session = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myEmail, password);
            }
        });

        Message message = prepareMessage(session, myEmail, reciEmail);
        Transport.send(message);
        System.out.println("Done");
    }

    private  Message prepareMessage(Session session, String myEmail, String sendersMail) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(sendersMail));
            message.setSubject(subject);
            message.setText(TextBody);
            return message;
        } catch (Exception e) {
            Logger.getLogger(SendGmail.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
}


class FileManeger {

  //save and read from the text file

  //save a string in to a text file
    public void saveToTextFile(String string,String fileName) {
        try {

          //write to the text file
           FileWriter myWriter = new FileWriter(".\\Text Files\\"+fileName+".txt",true);
            myWriter.write("\n"+string);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
            //cach if any exeptions occurs
        } catch (IOException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
      }

      //read from the text file
      public ArrayList<String> readFromTextFile(String fileName) {
        ArrayList<String> recipiensList=new ArrayList<>();
        try {
          FileReader reader = new FileReader(".\\Text Files\\"+fileName+".txt");
          BufferedReader buffReader = new BufferedReader(reader);
          String line=null;
          while ((line=buffReader.readLine())!=null) {
            recipiensList.add(line);
          }
          buffReader.close();
        }catch(FileNotFoundException fnf){
          return null;
        } 
        catch (Exception e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
        return recipiensList;
      }


      //seriallize multiple objects in to an array and save to a file
    public void seriallizeThis(Object obj,String fileName,String filePath,boolean append) {
      Object objToSeriallize=new Object();
      if (append) {
        ArrayList<Object> objList=new ArrayList<>();
        if ((new File(filePath+fileName+".ser").isFile())) {
          objList=(ArrayList<Object>)deSeriallizeThis(filePath+fileName);
        }
        objList.add(obj);
        objToSeriallize=objList;
      }
      else {
        objToSeriallize=obj;
      }

      try{
        Files.createDirectories(Paths.get(filePath));  //creat a folder if not already exists
        FileOutputStream fileStream = new FileOutputStream(filePath+fileName+".ser");      
        ObjectOutputStream objSream= new ObjectOutputStream(fileStream);
        objSream.writeObject(objToSeriallize);
        objSream.close();
      }catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }
      
    }
    //deserialize the saved emails of the given day and print the email subject and Recipien email adress
    public Object deSeriallizeThis(String fileName) {

        
        try (FileInputStream fileStream = new FileInputStream(fileName+".ser")) {
          ObjectInputStream objStream=new ObjectInputStream(fileStream);
          Object obj=objStream.readObject();
          return obj;
          }catch (ClassNotFoundException | IOException e){
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
        return null;
      }
    }    

    

    //Storing all the Recipients with birthdays in a binary search tree , 
    //so insertion and sesarch will be a little fasterr than storing in an array
class BirthdayList {
        private static BirthdayList bdlist;
        //i made it a singleton class becase i will not need multiple objects of this class i i want to make sure that 
        //whenever a new object is created it will be the same object
        public static BirthdayList getBirthdayList() {
            if (bdlist==null) {
                bdlist=new BirthdayList();
            }
            return bdlist;
        }
    
        class Node {//this will creat these nodes for every bith date
            ArrayList<Recipients> todayBD=new ArrayList<>();//this list will contain all the reciptions with that date of birth
            int key;
            Node left, right;
     
            public Node(int k,Recipients recipient)
            {
                key = k;
                left = right = null;
                todayBD.add(recipient);
            }
        }
    
        Node root;
        private BirthdayList() {
            root=null;
        }
    
        //to ad a new recipient
        public void add(OfficeFriendRecipients recipient) {
            //System.out.println("added to bd list"+recipient.getName());
            int key=genKey(recipient.getBirthDay());
            root=insert(root, key, recipient);
        }
        public void add(PersonalRecipients recipient) {
            //System.out.println("added to bd list"+recipient.getName());
            int key=genKey(recipient.getBirthDay());
            root=insert(root, key, recipient);
        }
    
        private Node insert(Node root,int key,Recipients recipient) {
            if (root==null) {
                root=new Node(key, recipient);
            }
            else if (key==root.key) {
                root.todayBD.add(recipient);
            }
            else if (key<root.key) {
                root.left=insert(root.left, key, recipient);
            }
            else if(key>root.key){
                root.right=insert(root.right, key, recipient);
            }
            return root;
        }
    
    
        //to get a list of recipient with the input birth day
        public ArrayList<Recipients> getEmailsOfThisBD(String birthDay) {
            int key=genKey(birthDay);
            Node todayNode=search(root, key);
            if (todayNode==null) {
                return null;
            }
            return todayNode.todayBD;
        }
        private Node search(Node root,int key) {
            if (root==null || root.key==key) {
                return root;
            }
            else if (root.key<key) {
                return search(root.right, key);
            }
            return search(root.left, key);
        }
        // make a unique key from the birth date , ex : 2000/08/16 =0816
        private int genKey(String birthDay) {
            String[] dateList=birthDay.split("[/]",0);
            int key=Integer.parseInt(dateList[1]+dateList[2]);
            return key;
        }
    }
    
        
class BDWishes {
        private ArrayList<Recipients> todayBD=new ArrayList<>();
        private SendMails sendMails=new SendGmailsAdupter();
        private BirthdayList birthdayList=BirthdayList.getBirthdayList();
        static String date=new String();
        private FileManeger file = new FileManeger();
        //hashmap that will later saved by serialization that will keep the date of last bd wished
        //so the same person wont recieve a bd wish email again on the same day
        static Hashtable<String, String> lastBDwishDate = new Hashtable<>(); 
    
        public BDWishes(String today) {
            this.date=today;
            this.todayBD =birthdayList.getEmailsOfThisBD(today);
            //if there is a last bd hashtable file , read it 
            if ((new File(".\\Emails\\"+"Last BDwish dates"+".ser").isFile())) {
                lastBDwishDate=(Hashtable<String, String>)file.deSeriallizeThis(".\\Emails\\"+"Last BDwish dates");
            }
            
        }
        public void sendWish() {
            if (todayBD==null) {
                return;  //if there is no bd today ,do nothing
            }
            for (Recipients recipients : todayBD) {
                //check if we have send any wishes to this recipient befor
                if (lastBDwishDate.containsKey(recipients.getEmailAddress())) {
                    //if have check if the date of last wish is the same as the current date
                    if (lastBDwishDate.get(recipients.getEmailAddress()).equals(date)) {
                        //if the date is the same ,do not send the wish
                        continue;
                    }
                }
                //if we have not send any wishes to this recipient before ,send the wish
                //and save the date of the last wish hashmap
                lastBDwishDate.put(recipients.getEmailAddress(), date);
                send(recipients);
    
            }
            //save the hashmap to file
            file.seriallizeThis(lastBDwishDate, "Last BDwish dates", ".\\Emails\\", false);
        }
        protected void send(Recipients recipient) {
            String messege;
            String subject="Birthday Wish";
            if (recipient instanceof PersonalRecipients) {
                messege="Hugs and love on your birthday. \n-Ginushmal";
            } else {
                messege="Wish you a Happy Birthday.\n-Ginushmal";
            }
            
            Email email=new Email(recipient, subject, messege);
            sendMails.SendMail(email);
        }
        
    }

    //made it as a singleten class becaouse we dont need more than a one object of this class
class GetLocalDate {
    private static String[] todayIs;
    private static GetLocalDate getLocalDate;
    private GetLocalDate() {     
        //get the the local date from the system and make it a string and get the date part and split it from "-" to get an array of [yyyy,mm,dd]  
        GetLocalDate.todayIs =(java.time.LocalDateTime.now()).toString().substring(0,10).split("[-]",0);
    }
    public static GetLocalDate date() {
        if (getLocalDate==null) {
            getLocalDate=new GetLocalDate();
        }
        return getLocalDate;
    }
    public  String[] getDate() {
        return todayIs;
    }
}
// create more classes needed for the implementation (remove the public access
// modifier from classes when you submit your code)

