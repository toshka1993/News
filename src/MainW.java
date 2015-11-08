import org.jsoup.Jsoup;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainW extends JFrame implements ActionListener{

    private static String s_conect_people = "http://people.onliner.by/";
    private static String s_conect_auto = "http://auto.onliner.by/";
    private static String s_conect_tech = "http://tech.onliner.by/";
    private static String s_conect_realt = "http://realt.onliner.by/";


    private List<FeedMessage> entries_people = new ArrayList<>();
    private List<FeedMessage> entries_auto = new ArrayList<>();
    private List<FeedMessage> entries_tech = new ArrayList<>();
    private List<FeedMessage> entries_realt = new ArrayList<>();

    public MainW() throws IOException {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        entries_people = getListNews(s_conect_people);
        entries_auto = getListNews(s_conect_auto);
        entries_tech = getListNews(s_conect_tech);
        entries_realt = getListNews(s_conect_realt);

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

        RssFeedTableModel mPeople = new RssFeedTableModel();
        mPeople.updateTable(entries_people);
        JTable tPeople = CreateTable(mPeople, "People");
        addMouseListener(tPeople, entries_people);

        RssFeedTableModel mAuto = new RssFeedTableModel();
        mAuto.updateTable(entries_auto);
        JTable tAuto = CreateTable(mAuto, "Auto");
        addMouseListener(tAuto, entries_auto);

        RssFeedTableModel mTech = new RssFeedTableModel();
        mTech.updateTable(entries_tech);
        JTable tTech = CreateTable(mTech, "Technology");
        addMouseListener(tTech, entries_tech);

        RssFeedTableModel mRealt = new RssFeedTableModel();
        mRealt.updateTable(entries_realt);
        JTable tRealt = CreateTable(mRealt, "Realt");
        addMouseListener(tRealt, entries_realt);

        Timer timer = new Timer(0, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    entries_people = getListNews(s_conect_people);
                    entries_auto = getListNews(s_conect_auto);
                    entries_tech = getListNews(s_conect_tech);
                    entries_realt = getListNews(s_conect_realt);

                    mPeople.updateTable(entries_people);
                    mAuto.updateTable(entries_auto);
                    mTech.updateTable(entries_tech);
                    mRealt.updateTable(entries_realt);

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        timer.setDelay(10000); // delay for 30 seconds
        timer.start();

        setSize(520, 500);
        setUndecorated(true);
        setOpacity(0.85f);

    }

    public List<FeedMessage> getListNews(String s_conect) throws IOException {
        List<FeedMessage> entries = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(s_conect).get();
            Elements listContent = doc.select("figure.b-posts-1-item__image");

            entries.clear();
            for (Element element : listContent) {
                Element link = element.select("a[href]").first(); // a with href
                Element title = element.select("img[alt]").first();

                entries.add(new FeedMessage(title.attr("alt"), link.attr("href")));
            }
        }
        catch (Exception ex)
        {
            entries.clear();
        }
        return entries;
    }

    public JTable CreateTable (RssFeedTableModel model, String label){
        JTable table = new JTable(model);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(20,5));
        table.setRowHeight(30);

        table.getColumnModel().getColumn(0).setHeaderValue(label);

        JTableHeader header = table.getTableHeader();
        header.setOpaque(true);
        header.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        header.setForeground(Color.RED);
        header.setBackground(header.getBackground());
        Font titleFont = new Font("Arial", Font.BOLD, 10);
        header.setFont(titleFont);

        table.setTableHeader(header);

        table.setDefaultRenderer(String.class,new DefaultTableCellRenderer(){
            Color oddColor=new Color(0x25,0x25,0x25);
            Color evenColor=new Color(0x1a,0x1a,0x1a);
            Color titleColor=new Color(0x3a,0xa2,0xd7);


            public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
                super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
                setBackground(row%2==0?oddColor:evenColor);
                setForeground(titleColor);
                // setFont(font);
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));

        getContentPane().add(scrollPane,BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                setShape(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),20,20));
            }
        });
        table.setSize(520,100);
        return table;
    }

    public void addMouseListener (JTable table, List<FeedMessage> list)
    {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                try {
                    URI uri = new URI(list.get(row).link);

                    openUri(uri);
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            private void openUri(URI uri) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(uri);
                    } catch (IOException e) { /* TODO: error handling */ }
                } else { /* TODO: error handling */ }
            }

        });
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
