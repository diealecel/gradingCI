import java.util.*;
import java.io.*;

// Note: registration.csv should be sorted by increasing submission date.
// Additionally, the sorting in zipgrade.csv is nominal.

class grader {
  public static void main(String[] args) throws IOException {
    Scanner user = new Scanner(System.in);
    System.out.println("\nMake sure registration.csv is sorted by date.");
    System.out.print("Enter the date: ");
    Data intel = new Data(user.nextLine());
    intel.print(intel.revision);
  }
}

/*  The steps the Data class should follow are the following:

    1. create an ArrayList<String[]> with all data from registration.csv,
    2. restrict that data set only to entries pertaining to a certain date,
    3. eliminate duplicates within that restricted set, and
    4. reorder the restricted, filtered set so that it may fit the format.

    After successfully formatting registration.csv, zipgrade.csv follows:

    5. create an ArrayList<String[]> with all data from zipgrade.csv,
    6. delete duplicates via student ID,
    7. restrict that data set only to entries matching both sets by student ID,
    8. eliminate entries that only persist in registration.csv,
    9. reorder the restricted, filtered set from least to greatest student ID,
    10. reorder registration.csv in an equal manner so as to match the set,
    11. calculate scores using user-inputted material recall questions, and
    12. merge both sets.

    After creating an entry for scores.csv, the entry should be added correctly
    (manually) to the scores.csv (despite there being obvious duplicates to be
    merged) on the drive and then exported (without headings) as a new
    scores.csv.

    Finally, do the following:

    13. create an ArrayList<String[]> with all data from scores.csv, and
    14. merge all duplicates.
*/

class Data {
  ArrayList<String[]> fullSet, cutSet, cleanSet, orderSet, shinySet, niceSet;
  ArrayList<String[]> fullZ, filterZ, cutZ, niceZ;
  ArrayList<String[]> results, revision;
  ArrayList<String[]> before, after;

  Data(String a) throws IOException {
    Scanner pause = new Scanner(System.in);
    create();
    discriminate(a);
    clean();
    order();
    createZ();
    cloneZ();
    discriminateZ();
    cleanZ();
    organizeReg();
    organizeZip();
    score();
    merge();
    //System.out.println("\nAdequately arrange the scores.csv file now.");
    //System.out.println("\nAdditionally, ensure that it is sorted by ID.");
    //System.out.print("When complete, press any key followed by enter. ");
    //pause.next();
    //store()
  }

  // Fulfills first requirement.
  void create() throws IOException {
    fullSet = new ArrayList<String[]>();
    Scanner in = new Scanner(new File("registration.csv"));
    while(in.hasNext())
      fullSet.add(in.nextLine().split(","));
  }

  // Fulfills second requirement.
  void discriminate(String a) {
    cutSet = new ArrayList<String[]>();
    for(int b = 1; b <= fullSet.size(); b++)
      if(call('G', b, fullSet).indexOf(a) != -1)
        cutSet.add(fullSet.get(b - 1));
  }

  // Fulfills third requirement.
  void clean() {
    cleanSet = new ArrayList<String[]>();
    TreeSet<String> used = new TreeSet<String>();
    for(int a = 1; a <= cutSet.size(); a++)
      if(used.add(call('D', a, cutSet)))
        cleanSet.add(cutSet.get(a - 1));
  }

  // Fulfills fourth requirement.
  void order() {
    orderSet = new ArrayList<String[]>();
    String a = "";
    for(int b = 1; b <= cleanSet.size(); b++) {
      a += call('D', b, cleanSet) + ",";
      a += call('C', b, cleanSet) + ",";
      a += call('B', b, cleanSet) + ",";
      a += call('F', b, cleanSet) + ",";
      a += ",";
      for(char c = 'J'; c <= 'R'; c++)
        if(c != 'R')
          if(call(c, b, cleanSet).indexOf("I am not") == -1)
            a += call(c, b, cleanSet) + ",";
          else
            a += ",";
        else
          if(call(c, b, cleanSet).indexOf("I am not") == -1)
            a += call(c, b, cleanSet);
      orderSet.add(a.split(","));
      a = "";
    }
  }

  // Fulfills fifth requirement.
  void createZ() throws IOException {
    fullZ = new ArrayList<String[]>();
    Scanner inZ = new Scanner(new File("zipgrade.csv"));
    while(inZ.hasNext())
      fullZ.add(inZ.nextLine().split(","));
  }

  // Fulfills sixth requirement.
  void cloneZ() {
    filterZ = new ArrayList<String[]>();
    TreeSet<String> used = new TreeSet<String>();
    for(int a = 1; a <= fullZ.size(); a++)
      if(used.add(call('E', a, fullZ)))
        filterZ.add(fullZ.get(a - 1));
  }

  // Fulfills seventh requirement.
  void discriminateZ() {
    cutZ = new ArrayList<String[]>();
    TreeSet<String> registered = new TreeSet<String>();
    for(int a = 1; a <= orderSet.size(); a++)
      registered.add(call('A', a, orderSet));
    for(int a = 1; a <= filterZ.size(); a++)
      if(!registered.add(call('E', a, filterZ)))
        cutZ.add(filterZ.get(a - 1));
  }

  // Fulfills eighth requirement.
  void cleanZ() {
    shinySet = new ArrayList<String[]>();
    TreeSet<String> intersection = new TreeSet<String>();
    for(int a = 1; a <= cutZ.size(); a++)
      intersection.add(call('E', a, cutZ));
    for(int a = 1; a <= orderSet.size(); a++)
      if(!intersection.add(call('A', a, orderSet)))
        shinySet.add(orderSet.get(a - 1));
  }

  // Fulfills ninth requirement.
  void organizeReg() {
    niceSet = new ArrayList<String[]>();
    TreeMap<Integer, Integer> links = new TreeMap<Integer, Integer>();
    PriorityQueue<Integer> organizer = new PriorityQueue<Integer>();
    for(int a = 1; a <= shinySet.size(); a++) {
      links.put(Integer.parseInt(call('A', a, shinySet)), a);
      organizer.add(Integer.parseInt(call('A', a, shinySet)));
    }
    while(!organizer.isEmpty())
      niceSet.add(shinySet.get(links.get(organizer.poll()) - 1));
  }

  // Fulfills tenth requirement.
  void organizeZip() {
    niceZ = new ArrayList<String[]>();
    TreeMap<Integer, Integer> links = new TreeMap<Integer, Integer>();
    PriorityQueue<Integer> organizer = new PriorityQueue<Integer>();
    for(int a = 1; a <= cutZ.size(); a++) {
      links.put(Integer.parseInt(call('E', a, cutZ)), a);
      organizer.add(Integer.parseInt(call('E', a, cutZ)));
    }
    while(!organizer.isEmpty())
      niceZ.add(cutZ.get(links.get(organizer.poll()) - 1));
  }

  // Fulfills eleventh requirement.
  void score() throws IOException {
    results = new ArrayList<String[]>();
    Scanner user = new Scanner(System.in);
    TreeMap<Integer, Integer> key = new TreeMap<Integer, Integer>();
    TreeMap<Integer, Boolean> input = new TreeMap<Integer, Boolean>();
    PriorityQueue<Integer> material = new PriorityQueue<Integer>();
    System.out.print("How many material recall questions are there? ");
    int mrtot = user.nextInt();
    System.out.println("Enter the numbers of the material recall questions.");
    for(int a = 0; a < mrtot; a++)
      material.add(user.nextInt());
    System.out.print("How many questions are there in total? ");
    int tot = user.nextInt();
    for(int a = 1; a <= tot; a++)
      if(material.contains(a))
        key.put(a, 2);
      else
        key.put(a, 1);
    int b = 1;
    String c = "";
    int s = 0, mr = 0;
    for(int a = 1; a <= niceZ.size(); a++) {
      for(char d = 'O'; d <= (char)((int)'O' + (tot - 1) * 4); d += 4)
        if(call(d, a, niceZ).charAt(0) == 'C')
          input.put(b++, true);
        else
          input.put(b++, false);
      for(int e = 1; e <= key.size(); e++)
        if(input.get(e))
          if(key.get(e) == 2)
            mr += 2;
          else
            s += 1;
      c += "," + s + ",," + mr;
      results.add(c.split(","));
      b = 1;
      c = "";
      s = 0;
      mr = 0;
    }
  }

  // Fulfills twelfth requirement.
  void merge() {
    revision = new ArrayList<String[]>();
    String a = "";
    for(int b = 0; b < niceSet.size(); b++) {
      for(int c = 0; c < niceSet.get(b).length; c++)
        a += niceSet.get(b)[c] + ",";
      for(int c = 0; c < 14 - niceSet.get(b).length; c++)
        a += ",";
      for(int c = 0; c < results.get(b).length; c++)
        if(c != results.get(b).length - 1)
          a += results.get(b)[c] + ",";
        else
          a += results.get(b)[c];
      revision.add(a.split(","));
      a = "";
    }
  }

  // Fulfills thirteenth requirement.
  void store() throws IOException {
    before = new ArrayList<String[]>();
    Scanner inS = new Scanner(new File("scores.csv"));
    while(inS.hasNext())
      before.add(inS.nextLine().split(","));
  }

  // Fulfills fourteenth requirement, but is still a work in progress.
  void dupes() {
    after = new ArrayList<String[]>();
    TreeSet<String> existing = new TreeSet<String>();
    for(int a = 1; a <= before.size(); a++)
      if(!existing.add(call('A', a, before)))
        after.set(a - 2, combine(before.get(a - 2), before.get(a - 1)));
      else
        after.add(before.get(a - 1));
  }

  // Returns a String[] as a result of the merging of two String[] objects.
  // Still not complete. Implement date (both before and here)
  String[] combine(String[] a, String[] b) {
    String[] c = new String[Math.max(a.length, b.length)];
    for(int d = 0; d < c.length; d++)
      if(a[d].equals(""))
        c[d] = b[d];
      else if(b[d].equals(""))
        c[d] = a[d];
      else
        c[d] = a[d];
    return c;
  }

  // Returns String in a cell of any set using .xls coordinates.
  String call(char a, int b, ArrayList<String[]> c) {
    return c.get(b - 1)[a - 65];
  }

  // Prints any given set as output.csv.
  void print(ArrayList<String[]> a) throws IOException {
    PrintWriter out = new PrintWriter(new File("output.csv"));
    String b = "";
    for(int c = 0; c < a.size(); c++) {
      for(int d = 0; d < a.get(c).length; d++)
        if(d != a.get(c).length - 1)
          b += a.get(c)[d] + ",";
        else
          b += a.get(c)[d];
      out.println(b);
      b = "";
    }
    out.close();
  }
}
