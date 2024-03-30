import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.awt.image.BufferedImage;
import java.util.Scanner;


class ShapePanel extends JPanel {
    private Shape[] shapes;

    public ShapePanel(Shape[] shapes) {
        this.shapes = shapes;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Shape shape : shapes) {
            shape.draw(g);
        }
    }
}

interface Shape {
    void draw(Graphics g);
}

class Line implements Shape {
    private int x1, y1, x2, y2;

    public Line(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void draw(Graphics g) {
        g.drawLine(x1, y1, x2, y2);
    }
}

class Rectangle implements Shape {
    private int x, y, width, height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Graphics g) {
        g.drawRect(x, y, width, height);
    }
}

class Circle implements Shape {
    private int x, y, diameter;

    public Circle(int x, int y, int diameter) {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
    }

    @Override
    public void draw(Graphics g) {
        g.drawOval(x, y, diameter, diameter);
    }
}

class ShapeReader {
    public static Shape[] readShapes(String filename) {
        Shape[] shapes = ShapeReader.readShapesFromJSON("data.json");
        if (shapes == null) {
            shapes = ShapeReader.readShapesFromINI("data.ini");
        }

        return shapes;
    }

    private static Shape[] readShapesFromINI(String filename) {
        return null;
    }

    private static Shape[] readShapesFromJSON(String filename) {
        try {
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(filename));

            Shape[] shapes = new Shape[jsonArray.size()];

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject shapeObject = (JSONObject) jsonArray.get(i);
                String type = (String) shapeObject.get("type");
                JSONObject coordinates = (JSONObject) shapeObject.get("coordinates");

                switch (type) {
                    case "line":
                        shapes[i] = new Line(
                                ((Long) coordinates.get("x1")).intValue(),
                                ((Long) coordinates.get("y1")).intValue(),
                                ((Long) coordinates.get("x2")).intValue(),
                                ((Long) coordinates.get("y2")).intValue()
                        );
                        break;
                    case "rectangle":
                        shapes[i] = new Rectangle(
                                ((Long) coordinates.get("x")).intValue(),
                                ((Long) coordinates.get("y")).intValue(),
                                ((Long) coordinates.get("width")).intValue(),
                                ((Long) coordinates.get("height")).intValue()
                        );
                        break;
                    case "circle":
                        shapes[i] = new Circle(
                                ((Long) coordinates.get("x")).intValue(),
                                ((Long) coordinates.get("y")).intValue(),
                                ((Long) coordinates.get("diameter")).intValue()
                        );
                        break;
                }
            }

            return shapes;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}



class ShapeDisplayer {
    public static void displayShapes() {
        Shape[] shapes = ShapeReader.readShapes("data.json");
        if (shapes == null) {
            System.out.println("No Data for drawing shapes.");
            return;
        }

        JFrame frame = new JFrame("Drawing Shapes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        frame.add(new ShapePanel(shapes));
        frame.setVisible(true);
    }
}

class ShapeSaver {
    public static void saveShapes(String filename) {
        Shape[] shapes = ShapeReader.readShapes("data.json");
        if (shapes == null) {
            System.out.println("No Data for drawing shapes.");
            return;
        }
    
        JFrame frame = new JFrame("Drawing Shapes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200, 200);
        ShapePanel panel = new ShapePanel(shapes);
        frame.add(panel);
        frame.setVisible(true);
    
        panel.paintImmediately(0, 0, panel.getWidth(), panel.getHeight());
    
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D imageGraphics = image.createGraphics();
    
        panel.paint(imageGraphics);
        imageGraphics.dispose();
    
        try {
            ImageIO.write(image, "jpeg", new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Main {
    public static void main(String[] args) {

        System.out.print("Select mode to display <window|image>: ");
        Scanner input = new Scanner(System.in);
        String mode = input.nextLine().toLowerCase();
        input.close();

        switch (mode) {
            case "window":
                ShapeDisplayer.displayShapes();
                break;
            default:
                ShapeSaver.saveShapes(mode);
        }
    }
}