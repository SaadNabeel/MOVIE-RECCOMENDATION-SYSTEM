
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
static void manageWatchlist(Context ctx) {

    System.out.println("1. Add 2. Remove 3. View");
    int c = readInt(ctx);

    if (c == 3) {
        renderMovies(ctx.movies.stream()
        .filter(m -> ctx.activeUser.watchlist.contains(m.id))
        .collect(Collectors.toList()));
    } else {
        System.out.print("Movie ID: ");
        int id = readInt(ctx);

        if (c == 1) ctx.activeUser.watchlist.add(id);
        if (c == 2) ctx.activeUser.watchlist.remove(id);
    }

}

static void recommendMovies(Context ctx) {

    Map<String, Integer> genreScore = buildGenreScore(ctx);

    List<Movie> recs = ctx.movies.stream()
    .sorted((a, b) -> Double.compare(
    calculateScore(b, genreScore), calculateScore(a, genreScore)))
    .limit(10)
    .collect(Collectors.toList());

}
static void manageWatchlist(Context ctx) {

    System.out.println("1. Add 2. Remove 3. View");
    int c = readInt(ctx);

    if (c == 3) {
        renderMovies(ctx.movies.stream()
        .filter(m -> ctx.activeUser.watchlist.contains(m.id))
        .collect(Collectors.toList()));
    } else {
        System.out.print("Movie ID: ");
        int id = readInt(ctx);

        if (c == 1) ctx.activeUser.watchlist.add(id);
        if (c == 2) ctx.activeUser.watchlist.remove(id);
    }

}

static void recommendMovies(Context ctx) {

    Map<String, Integer> genreScore = buildGenreScore(ctx);

    List<Movie> recs = ctx.movies.stream()
    .sorted((a, b) -> Double.compare(
    calculateScore(b, genreScore), calculateScore(a, genreScore)))
    .limit(10)
    .collect(Collectors.toList());

}
static void viewHistory(Context ctx) {

renderMovies(ctx.activeUser.history.stream()

.map(id -> ctx.movies.get(id - 1))

.collect(Collectors.toList()));

}
static void compareMovies(Context ctx) {

System.out.print("First Movie ID: ");
int a = readInt(ctx);

System.out.print("Second Movie ID: ");
int b = readInt(ctx);

Movie m1 = ctx.movies.get(a - 1);
Movie m2 = ctx.movies.get(b - 1);

System.out.println("Comparison Result:");
printMovieDetail(m1);
printMovieDetail(m2);

}

static void analyticsDashboard(Context ctx) {

ctx.movies.stream().max(Comparator.comparingDouble(m -> m.rating))
.ifPresent(m -> System.out.println("Top Rated: " + m.title));

ctx.movies.stream().max(Comparator.comparingInt(m -> m.views))
.ifPresent(m -> System.out.println("Most Viewed: " + m.title));

Map<String, Long> genreStats =
ctx.movies.stream().collect(Collectors.groupingBy(m -> m.genre, Collectors.counting()));

genreStats.forEach((g, c) -> System.out.println(g + ": " + c));

}
static void userProfile(Context ctx) {

System.out.println("User: " + ctx.activeUser.name);
System.out.println("Ratings Given: " + ctx.activeUser.ratings.size());
System.out.println("Watchlist Size: " + ctx.activeUser.watchlist.size());
System.out.println("Recommendations Seen: " + ctx.activeUser.recommendationHistory.size());

}

static void switchUser(Context ctx) {

ctx.users.forEach(u -> System.out.println(u.id + ". " + u.name));

System.out.print("User ID: ");
int id = readInt(ctx);

ctx.activeUser = ctx.users.stream()
.filter(u -> u.id == id)
.findFirst()
.orElse(ctx.activeUser);

}

static void shutdown(Context ctx) {

long duration = (System.currentTimeMillis() - ctx.sessionStart) / 1000;
System.out.println("Session Duration: " + duration + " seconds");
System.out.println("System shutting down");
System.exit(0);

}
static Map<String, Integer> buildGenreScore(Context ctx) {

Map<String, Integer> score = new HashMap<>();

ctx.activeUser.ratings.forEach((id, r) -> {

if (id <= ctx.movies.size())
score.merge(ctx.movies.get(id - 1).genre, r, Integer::sum);

});

ctx.activeUser.watchlist.forEach(id -> {

if (id <= ctx.movies.size())
score.merge(ctx.movies.get(id - 1).genre, 2, Integer::sum);

});

return score;

}

static double calculateScore(Movie m, Map<String, Integer> s) {

return m.rating * 2.5 + m.views * 0.01 + s.getOrDefault(m.genre, 0) * 3;

}

static void explainRecommendations(List<Movie> list, Map<String, Integer> score) {

System.out.println("Why these movies?");

list.forEach(m ->
System.out.println(m.title + " → Genre affinity: " + score.getOrDefault(m.genre, 0))
);

}
static void renderMovies(List<Movie> list) {

if (list.isEmpty()) {
System.out.println("No movies found");
return;
}

list.forEach(CineVerse::printMovieSummary);

}

static void printMovieSummary(Movie m) {

System.out.println(m.id + ". " + m.title + " | " + m.genre + " | " + m.year + " | ★" + m.rating);

}

static void printMovieDetail(Movie m) {

System.out.println("------");

System.out.println("Title: " + m.title);
System.out.println("Genre: " + m.genre);
System.out.println("Director: " + m.director);
System.out.println("Year: " + m.year);
System.out.println("Rating: " + m.rating);
System.out.println("Views: " + m.views);

}
static int readInt(Context ctx) {

while (true) {
try {
return Integer.parseInt(ctx.scanner.nextLine());
} catch (Exception e) {
System.out.print("Enter valid number: ");
}
}

}

static int parseIntSafe(String s) {
try { return Integer.parseInt(s.trim()); }
catch (Exception e) { return 0; }
}

static double parseDoubleSafe(String s) {
try { return Double.parseDouble(s.trim()); }
catch (Exception e) { return 0.0; }
}
