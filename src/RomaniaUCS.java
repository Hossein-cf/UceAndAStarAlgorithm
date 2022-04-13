import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class RomaniaUCS {
// hossein shakeri 985361030
    public static void main(String[] args) {     //------------------------------------MAIN--------------------------------

        RomaniaProblem problem = new RomaniaProblem("Lugoj", "Bucharest");
        boolean flag = true;
        System.out.println("برای انجام الگوریتم ucs عدد 1 و برای انجام الگوریتم A* عدد 2 و بریا خروج عدد 3 را وارد کنید.");
        do {
            Scanner scanner = new Scanner(System.in);
            System.out.print("عدد را وارد کنید : ");
            int input = scanner.nextInt();
            switch (input) {
                case 2: {
                    SearchAgent_Star agent = new SearchAgent_Star();
                    Node node = agent.solve(problem);
                    node.printBackTraceAStar();
                    System.out.println("}");
                    System.out.println("-------------------------------------------------------------");
                    break;
                }
                case 1: {
                    SearchAgent_UCS agent = new SearchAgent_UCS();
                    Node node = agent.solve(problem);
                    node.printBackTraceUcs();
                    System.out.println("}");
                    System.out.println("-------------------------------------------------------------");
                    break;
                }
                case 3: {
                    flag = false;
                    break;
                }
            }
        } while (flag);
    }
}

class SearchAgent_Star {
    public Node solve(RomaniaProblem problem) {
        int expandedNumber = 0;
        int exploredNumber = 0;
        List<Node> explored = new ArrayList<>();
        Node startNode = new Node(problem.getStartState());
        PriorityQueue<Node> expanded = new PriorityQueue<Node>(new Comparator<Node>() {
            @Override
            public int compare(Node i, Node j) {
                if (i.f > j.f) return 1;
                else if (i.f < j.f) return -1;
                return 0;
            }
        });
        expanded.add(startNode);
        do {
            Node current = expanded.poll();
            if (explored.contains(current)) {
                continue;
            }
            explored.add(current);
            if (problem.goalTest(current.state)) {
                System.out.println("{\n\texpanded number : " + expandedNumber + ",\n\texplored number : " + exploredNumber + ",");
                return current;
            }
            expandedNumber++;
            Action actions[] = Map.successorFunction(current.state);
            for (Action a : actions) {
                Node child = new Node(a.successor);

                child.parent = current;
                child.pathCost = current.pathCost + a.cost;
                child.f = child.pathCost + child.h;
                AtomicBoolean test = new AtomicBoolean(false);
                explored.forEach(node -> {
                    if (node.parent != null && node.parent.state.equals(child.parent) && node.f <= child.f || node.state.city.equals(a.successor.city)) {
                        test.set(true);
                    }
                });
                if (test.get()) {
                    continue;
                } else {
                    if (expanded.contains(child)) {
                        expanded.remove(child);
                    }
                    expanded.add(child);
                    exploredNumber++;
                }
            }
        } while (!expanded.isEmpty());
        return new Node(new State(" "));
    }
}

class SearchAgent_UCS {           //------------------------------Intelligent Agent-----------------------------------------

    public Node solve(RomaniaProblem problem) {
        int expandedNumber = 0;
        int exploredNumber = 0;
        Node start = new Node(problem.getStartState());
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(start);
        exploredNumber++;
        //define and initialize START NODE, FRINGE and EXPLORED SET (for GRAPH SEARCH)
        List<Node> exploredNodes = new ArrayList<>();
        do {
            //select a node from fringe for expansion based on UCS (use PriorityQueue)
            Node currentNode = queue.poll();

            if (currentNode == null)
                break;
            //check the node is explored or not
            if (isExplored(exploredNodes, currentNode)) continue;
            //insert the node into explored list
            exploredNodes.add(currentNode);
            //do goal test,
            //if true, return current node
            if (problem.goalTest(currentNode.state)) {
                System.out.println("{\n\texpanded number : " + expandedNumber + ",\n\texplored number : " + exploredNumber + ",");
                return currentNode;
            }
            //else insert children obtained from successor function into fringe
            Action actions[] = Map.successorFunction(currentNode.state);
            expandedNumber++;
            for (Action action : actions) {
                Node n = new Node(action.successor);
                n.parent = currentNode;
                n.pathCost += action.cost + n.parent.pathCost;
                queue.add(n);
                exploredNumber++;
            }
        } while (true);
        Node n = new Node(new State(""));   //if not find the goal return an empty node
        return n;
    }

    private boolean isExplored(List<Node> exploredNodes, Node currentNode) {
        AtomicBoolean explored = new AtomicBoolean(false);
        exploredNodes.forEach(node -> {
            if (node.state.city.equals(currentNode.state.city))
                explored.set(true);
        });
        if (explored.get())
            return true;
        return false;
    }
}

class State {                        //-------------------States in problem formulation-------------------------------------
    public String city;

    public State(String city) {
        this.city = city;
    }

    public String toString() {
        return this.city;
    }
}

class Action {                      //--------------------Actions in problem formulation------------------------------------
    public State successor;
    public double cost;

    public Action(State succ, double c) {
        this.successor = succ;
        this.cost = c;
    }

    public String toString() {
        return (successor.toString() + ", " + cost);
    }
}

class Node implements Comparable<Node> {                         //-----------------------Nodes in the search tree----------------------------------------
    public final State state;
    public double pathCost;
    public double h;
    public double f;
    public Node parent;

    public Node(State stValue) {
        this.state = stValue;
        this.h = heuristic(stValue);
        this.pathCost = 0;
        this.parent = null;
    }

    public double heuristic(State st) {
        double heuristic = 0;
        switch (st.city) {
            case "Arad":
                heuristic = 366;
                break;
            case "Zerind":
                heuristic = 374;
                break;
            case "Oradea":
                heuristic = 380;
                break;
            case "Sibiu":
                heuristic = 253;
                break;
            case "Fagaras":
                heuristic = 176;
                break;
            case "Rimnicu Vilcea":
                heuristic = 193;
                break;
            case "Pitesti":
                heuristic = 100;
                break;
            case "Timisoara":
                heuristic = 329;
                break;
            case "Lugoj":
                heuristic = 244;
                break;
            case "Mehadia":
                heuristic = 241;
                break;
            case "Drobeta":
                heuristic = 242;
                break;
            case "Craiova":
                heuristic = 160;
                break;
            case "Bucharest":
                heuristic = 0;
                break;
            case "Giurgiu":
                heuristic = 77;
                break;
        }
        return heuristic;
    }

    @Override
    public int compareTo(Node n) {
        if (this.pathCost == n.pathCost)
            return 0;
        else if (this.pathCost > n.pathCost)
            return 1;
        else
            return -1;
    }

    @Override
    public String toString() {
        return this.state.city;
    }

    public void printBackTraceUcs() {
        if (parent != null)
            parent.printBackTraceUcs();
        System.out.println("\t" + this.state.city + ":{path cost value : " + pathCost + "},");
    }

    public void printBackTraceAStar() {
        if (parent != null)
            parent.printBackTraceAStar();
        System.out.println("\t" + this.state.city + ":{" + "path cost value : " + pathCost + ",\t heuristic value : " + h + "},");
    }
}

class RomaniaProblem {               //----------------------------------problem formulation----------------------------------
    State startState;
    State goalState;
    //state space defined by initial state, actions, transition model

    public RomaniaProblem(String start, String goal) {
        startState = new State(start);
        goalState = new State(goal);
    }

    public State getStartState() {
        return startState;
    }

    public boolean goalTest(State st) {
        if (st.city.equals(this.goalState.city)) return true;
        else
            return false;
    }


}

class Map {
    public static Action[] successorFunction(State st) {
        Action[] children = new Action[]{};

        if (st.city.equals("Arad"))
            children = new Action[]{
                    new Action(new State("Zerind"), 75),
                    new Action(new State("Sibiu"), 140),
                    new Action(new State("Timisoara"), 118)
            };
        else if (st.city == "Zerind")
            children = new Action[]{
                    new Action(new State("Arad"), 75),
                    new Action(new State("Oradea"), 71)
            };
        else if (st.city == "Oradea")
            children = new Action[]{
                    new Action(new State("Zerind"), 71),
                    new Action(new State("Sibiu"), 151)
            };
        else if (st.city == "Sibiu")
            children = new Action[]{
                    new Action(new State("Arad"), 140),
                    new Action(new State("Fagaras"), 99),
                    new Action(new State("Oradea"), 151),
                    new Action(new State("Rimnicu Vilcea"), 80),
            };
        else if (st.city == "Fagaras")
            children = new Action[]{
                    new Action(new State("Sibiu"), 99),
                    new Action(new State("Bucharest"), 211)
            };
        else if (st.city == "Rimnicu Vilcea")
            children = new Action[]{
                    new Action(new State("Sibiu"), 80),
                    new Action(new State("Pitesti"), 97),
                    new Action(new State("Craiova"), 146)
            };
        else if (st.city == "Pitesti")
            children = new Action[]{
                    new Action(new State("Rimnicu Vilcea"), 97),
                    new Action(new State("Bucharest"), 101),
                    new Action(new State("Craiova"), 138)
            };
        else if (st.city == "Timisoara")
            children = new Action[]{
                    new Action(new State("Arad"), 118),
                    new Action(new State("Lugoj"), 111)
            };
        else if (st.city == "Lugoj")
            children = new Action[]{
                    new Action(new State("Timisoara"), 111),
                    new Action(new State("Mehadia"), 70)
            };
        else if (st.city == "Mehadia")
            children = new Action[]{
                    new Action(new State("Lugoj"), 70),
                    new Action(new State("Drobeta"), 75)
            };
        else if (st.city == "Drobeta")
            children = new Action[]{
                    new Action(new State("Mehadia"), 75),
                    new Action(new State("Craiova"), 120)
            };
        else if (st.city == "Craiova")
            children = new Action[]{
                    new Action(new State("Drobeta"), 120),
                    new Action(new State("Rimnicu Vilcea"), 146),
                    new Action(new State("Pitesti"), 138)
            };
        else if (st.city == "Bucharest")
            children = new Action[]{
                    new Action(new State("Pitesti"), 101),
                    new Action(new State("Giurgiu"), 90),
                    new Action(new State("Fagaras"), 211)
            };
        else if (st.city == "Giurgiu")
            children = new Action[]{
                    new Action(new State("Bucharest"), 90)
            };

        return children;

    }

}