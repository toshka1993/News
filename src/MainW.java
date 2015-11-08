import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.*;

public class MainW extends JFrame implements ActionListener{

    private static String s_conect_people = "http://people.onliner.by/";
    private static String s_conect_auto = "http://auto.onliner.by/";
    private static String s_conect_tech = "http://tech.onliner.by/";
    private static String s_conect_realt = "http://realt.onliner.by/";


    public MainW() throws IOException {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Container panel = getContentPane();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Onliner");
        Font titleFont = new Font("Arial", Font.BOLD, 15);
        titleLabel.setFont(titleFont);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.DARK_GRAY);
        titleLabel.setPreferredSize(new Dimension(0, 20));

        getContentPane().add(titleLabel, BorderLayout.NORTH);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
            }
        });

        RssJTable tPeople = new RssJTable(s_conect_people, "People");
        RssJTable tAuto = new RssJTable(s_conect_auto, "Auto");
        RssJTable tTech = new RssJTable(s_conect_tech, "Technology");
        RssJTable tRealt = new RssJTable(s_conect_realt, "Realt");

        getContentPane().add(tPeople);
        getContentPane().add(tAuto);
        getContentPane().add(tTech);
        getContentPane().add(tRealt);

        CreateScroll(tPeople);
        CreateScroll(tAuto);
        CreateScroll(tTech);
        CreateScroll(tRealt);

        setSize(520, 500);
        setUndecorated(true);
        setOpacity(0.85f);

    }

    private void CreateScroll(JTable table)
    {
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        getContentPane().add(scrollPane, BorderLayout.CENTER);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return Date.class;
        }
        return Object.class;
    }
}
