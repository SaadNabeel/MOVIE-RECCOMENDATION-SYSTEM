
public class CineVerse {

    static class Movie {

        int id;
        String title;
        String genre;
        String director;
        int year;
        double rating;
        int views;

        Movie(int id, String title, String genre, String director, int year,
        double rating, int views) {

            this.id = id;
            this.title = title;
            this.genre = genre;
            this.director = director;
            this.year = year;
            this.rating = rating;
            this.views = views;

        }

    }

}
static class User {

    int id;
    String name;
    Map<Integer, Integer> ratings = new HashMap<>();
    Set<Integer> watchlist = new HashSet<>();
    Deque<Integer> history = new ArrayDeque<>();
    List<Integer> recommendationHistory = new ArrayList<>();

    User(int id, String name) {

        this.id = id;
        this.name = name;

    }

}

static class Context {

    List<Movie> movies = new ArrayList<>();
    List<User> users = new ArrayList<>();
    User activeUser;
    Scanner scanner = new Scanner(System.in);
    long sessionStart = System.currentTimeMillis();

}
public static void main(String[] args) {

    Context ctx = new Context();
    loadMovies(ctx);
    seedUsers(ctx);
    ctx.activeUser = ctx.users.get(0);
    showWelcome();
    mainMenu(ctx);

}

static void showWelcome() {

    System.out.println("==============================================");
    System.out.println("-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-");
    System.out.println("            CineVerse Movie Platform            ");
    System.out.println("==============================================");

}
static void loadMovies(Context ctx) {

    try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {

        String line;
        while ((line = br.readLine()) != null) {

            if (line.trim().isEmpty() || line.startsWith("ID")) continue;

            String[] p = line.split("\\|");
            if (p.length < 7) continue;

            ctx.movies.add(new Movie(
                parseIntSafe(p[0]),
                p[1].trim(),
                p[2].trim(),
                p[3].trim(),
                parseIntSafe(p[4]),
                parseDoubleSafe(p[5]),
                parseIntSafe(p[6])
            ));

        }

    } catch (Exception e) {

        System.out.println("Critical Error: Unable to load data.txt");
        System.exit(0);

    }

}
static void seedUsers(Context ctx) {

    for (int i = 1; i <= 15; i++) {
        ctx.users.add(new User(i, "User_" + i));
    }

}

static void mainMenu(Context ctx) {

    while (true) {

        printHeader(ctx);
        printMenu();
        int choice = readInt(ctx);

        switch (choice) {

            case 1 -> viewAllMovies(ctx);
            case 2 -> searchMovie(ctx);
            case 3 -> filterByGenre(ctx);
            case 4 -> sortMovies(ctx);
            case 5 -> viewMovieDetails(ctx);
            case 6 -> rateMovie(ctx);
            case 7 -> manageWatchlist(ctx);
            case 8 -> recommendMovies(ctx);
            case 9 -> viewHistory(ctx);
            case 10 -> compareMovies(ctx);
            case 11 -> analyticsDashboard(ctx);
            case 12 -> userProfile(ctx);
            case 13 -> switchUser(ctx);
            default -> shutdown(ctx);

        }

    }

}
static void printHeader(Context ctx) {

    System.out.println("\n------------------------");
    System.out.println("Active User: " + ctx.activeUser.name);
    System.out.println();

}

static void printMenu() {

    System.out.println("1. View All Movies");
    System.out.println("2. Search Movie");
    System.out.println("3. Filter by Genre");
    System.out.println("4. Sort Movies");
    System.out.println("5. View Movie Details");
    System.out.println("6. Rate Movie");
    System.out.println("7. Manage Watchlist");
    System.out.println("8. Smart Recommendations");
    System.out.println("9. Recently Watched");
    System.out.println("10. Compare Two Movies");
    System.out.println("11. Analytics Dashboard");
    System.out.println("12. User Profile Summary");
    System.out.println("13. Switch User");
    System.out.println("0. Exit");
    System.out.print("Choose option: ");

}
static void viewAllMovies(Context ctx) {

    renderMovies(ctx.movies);

}

static void searchMovie(Context ctx) {

    System.out.print("Enter keyword: ");
    String k = ctx.scanner.nextLine().toLowerCase();

    renderMovies(ctx.movies.stream()
        .filter(m -> m.title.toLowerCase().contains(k))
        .collect(Collectors.toList())
    );

}

static void filterByGenre(Context ctx) {

    System.out.print("Enter genre or All: ");
    String g = ctx.scanner.nextLine();

    renderMovies(ctx.movies.stream()
        .filter(m -> g.equalsIgnoreCase("All") || m.genre.equalsIgnoreCase(g))
        .collect(Collectors.toList())
    );

}

static void sortMovies(Context ctx) {

    System.out.print("Sort by rating/views/year: ");
    String s = ctx.scanner.nextLine();

    List<Movie> list = new ArrayList<>(ctx.movies);

    if (s.equalsIgnoreCase("rating"))
        list.sort((a, b) -> Double.compare(b.rating, a.rating));

    else if (s.equalsIgnoreCase("views"))
        list.sort((a, b) -> b.views - a.views);

    else if (s.equalsIgnoreCase("year"))
        list.sort((a, b) -> b.year - a.year);

    renderMovies(list);

}
static void viewMovieDetails(Context ctx) {

    System.out.print("Movie ID: ");
    int id = readInt(ctx);

    ctx.movies.stream()
        .filter(m -> m.id == id)
        .findFirst()
        .ifPresentOrElse(m -> {
            printMovieDetail(m);
            ctx.activeUser.history.addFirst(id);
            if (ctx.activeUser.history.size() > 10)
                ctx.activeUser.history.removeLast();
        }, () -> System.out.println("Movie not found"));

}

static void rateMovie(Context ctx) {

    System.out.print("Movie ID: ");
    int id = readInt(ctx);

    System.out.print("Rating (1-5): ");
    int r = readInt(ctx);

    if (r < 1 || r > 5) {
        System.out.println("Invalid rating");
        return;
    }

    ctx.activeUser.ratings.put(id, r);

}
static void rateMovie(Context ctx) {

    System.out.print("Movie ID: ");
    int id = readInt(ctx);

    System.out.print("Rating (1-5): ");
    int r = readInt(ctx);

    if (r < 1 || r > 5) {
        System.out.println("Invalid rating");
        return;
    }

    ctx.activeUser.ratings.put(id, r);
    System.out.println("Rating recorded successfully");

}
