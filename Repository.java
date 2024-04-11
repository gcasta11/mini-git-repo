// Gabriela Castaneda
// 11/1/2023
// CSE 123
// P1
// TA: Riva Gore

// This class acts as a repository and allows you, the user, to perform various
// commits and actions on these commits including dropping, synchronizing, inspecting,
// and creating commits and commit history.
import java.util.*;
import java.text.SimpleDateFormat;
public class Repository {

    /**
     * TODO: Implement your code here.
     */
    private String name;
    private Commit head;

    // This constructor method will throw an IllegalArgumentException if
    // the name provided is null.
    // Parameters:
    // - name is the name of the repository
    public Repository(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Repository name cannot be null.");

        }
        this.name = name;
        this.head = null;

    }

    // This method returns an ID of the current commit, and returns null if
    // there are no commits in the repository
    public String getRepoHead() {
        if (head == null) {
            return null;
        }

        return head.id;
    }

    // This method returns the number of commits in the repository
    public int getRepoSize() {
        return countCommits(head);
    }

    // This helper method returns the count of commits in the repository
    private int countCommits(Commit commit) {
        if (commit == null) {
            return 0;
        }

        return 1 + countCommits(commit.past);
    }

    // This method returns a string interpretation of the repository including
    // the repositories name and current head information.
    public String toString() {
        if (head == null) {
            return name + " - No commits";
        }
        return name + " - Current head: " + head.toString();
    }

    // This method returns true if a specific ID exists in the repository.
    // Parameters:
    // - targetID is the desired ID being searched for in the repository
    public boolean contains(String targetId) {
        return containsCommit(targetId, head);
    }

    // This helper method is used to search for a specific ID.
    // Parameters:
    // - targetID is the desired ID being searched for in the repository
    // - commit is the action or commit made by the user
    private boolean containsCommit(String targetId, Commit commit) {
        if (commit == null) {
            return false;
        }
        if (commit.id.equals(targetId)) {
            return true;
        }
        return containsCommit(targetId, commit.past);
    }

    // This method returns the most recent commit history. Method will throw an
    // IllegalArgumentException if there is a negative number of commits.
    // Parameters:
    // - n is the single commit being inspected
    public String getHistory(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be a positive integer.");
        }
        return getRecentHistory(n, head);
    }

    // This helper method is used to retrive the recent commit history.
    // Parameters:
    // - n is the single commit being inspected
    // - commit is the action or commit made by the user
    private String getRecentHistory(int n, Commit commit) {
        if (n <= 0 || commit == null) {
            return "";
        }
        return commit.toString() + "\n" + getRecentHistory(n - 1, commit.past);
    }

    // This method returns an ID of the newly created head of the repository.
    // Parameters:
    // - message is the message returned depending on the ID of the commit
    public String commit(String message) {
        Commit newCommit = new Commit(message, head);
        head = newCommit;
        return newCommit.id;
    }

    // This method returns false if the head commit is empty. If not, method returns true
    // and allows you to remove a specific commit with its ID
    public boolean drop(String targetId) {
        if (head == null || !contains(targetId)) {
            return false;
        }
        if (head.id.equals(targetId)) {
            head = head.past;
        } else {
            dropCommit(targetId, head);
        }
        return true;
    }

    // This helper method is used to remove a commit from the repository.
    // Parameters:
    // - targetID is the desired ID being searched in the Repository
    // - commit is the action or commit being made by the user
    private void dropCommit(String targetId, Commit commit) {
        if (commit.past != null && commit.past.id.equals(targetId)) {
            commit.past = commit.past.past;
        } else {
            dropCommit(targetId, commit.past);
        }
    }

    // This method merges the commit history of another repository into the
    // current one.
    // Parameters:
    // - other is the other repository
    public void synchronize(Repository other) {
        if (other == null || other.head == null) {
            return;
        }

        if (head == null) {
            head = copyCommits(other.head);
        } else {
            Commit currentCommit = head;
            while (currentCommit.past != null) {
                currentCommit = currentCommit.past;
            }
            currentCommit.past = copyCommits(other.head);
        }
        other.head = null;
    }

    private Commit copyCommits(Commit commit) {
        if (commit == null) {
            return null;
        }

        Commit newCommit = new Commit(commit.message);
        newCommit.id = commit.id;
        newCommit.timeStamp = commit.timeStamp;
        newCommit.past = copyCommits(commit.past);

        return newCommit;
    }


    /**
     * DO NOT MODIFY
     * A class that represents a single commit in the repository.
     * Commits are characterized by an identifier, a commit message,
     * and the time that the commit was made. A commit also stores
     * a reference to the immediately previous commit if it exists.
     *
     * Staff Note: You may notice that the comments in this
     * class openly mention the fields of the class. This is fine
     * because the fields of the Commit class are public. In general,
     * be careful about revealing implementation details!
     */
    public class Commit {

        private static int currentCommitID;

        /**
         * The time, in milliseconds, at which this commit was created.
         */
        public final long timeStamp;

        /**
         * A unique identifier for this commit.
         */
        public final String id;

        /**
         * A message describing the changes made in this commit.
         */
        public final String message;

        /**
         * A reference to the previous commit, if it exists. Otherwise, null.
         */
        public Commit past;

        /**
         * Constructs a commit object. The unique identifier and timestamp
         * are automatically generated.
         * @param message A message describing the changes made in this commit.
         * @param past A reference to the commit made immediately before this
         *             commit.
         */
        public Commit(String message, Commit past) {
            this.id = "" + currentCommitID++;
            this.message = message;
            this.timeStamp = System.currentTimeMillis();
            this.past = past;
        }

        /**
         * Constructs a commit object with no previous commit. The unique
         * identifier and timestamp are automatically generated.
         * @param message A message describing the changes made in this commit.
         */
        public Commit(String message) {
            this(message, null);
        }

        /**
         * Returns a string representation of this commit. The string
         * representation consists of this commit's unique identifier,
         * timestamp, and message, in the following form:
         *      "[identifier] at [timestamp]: [message]"
         * @return The string representation of this collection.
         */
        @Override
        public String toString() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(timeStamp);

            return id + " at " + formatter.format(date) + ": " + message;
        }

        /**
         * Resets the IDs of the commit nodes such that they reset to 0.
         * Primarily for testing purposes.
         */
        public static void resetIds() {
            Commit.currentCommitID = 0;
        }
    }
}
