/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package module.Patient;
import module.Broadcastable;
import framework.GPSISDataMapper;
import framework.GPSISPopup;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

import mapper.PatientDMO;
import module.Patient.AddPatient;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import object.MedicalCondition;

/**
 *
 * @author skas
 */
public class ViewMedicalCondition extends GPSISPopup implements ActionListener{
    //it seems default values for static variables are initialised even before 
    //running the main program. this means anything that would depend on DB 
    //connections cant be intialised in default values.
    private static MedicalCondition [] medConds;
    
    private JList<Object> list;
    private DefaultListModel<Object> listModel;
    private JButton addButtonFld;
    private JButton delButtonFld;
    
    //Used a linkedHashSet to make sure no duplicate entries are added.
    private LinkedHashSet<MedicalCondition> medicalConditions;
    private Broadcastable parent;
    private int selectedRow;
    private JComboBox<?> comboBox;
    /*
     * This constructor takes in a Broadcastable object, this allows the class
     * to be able to communicate with the parent container that intialised it.
    */
    private ViewMedicalCondition _this;
    public ViewMedicalCondition(final Broadcastable parent,ArrayList<MedicalCondition> medConditions) 
    {
        //Should be an edit/ and for adding
        super("Medical Conditions");
        //assigning value to _this to be used in anonymous functions
        //Look up javascript 101 ;)
        _this = this;
        if(medConds == null)
        {
            medConds = MedicalCondition.getAllArray();
        }
        //TODO: Why not add shortcuts for add/delete?
        
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLayout(new MigLayout());
        this.setBackground(new Color(240, 240, 240));
        this.setAlwaysOnTop(true);
        
        //Get MedicalConditions and add them to listModel
        this.listModel = new DefaultListModel<Object>();
        this.medicalConditions = new LinkedHashSet<>();
        if(medConditions != null)
        {
            this.medicalConditions.addAll(medConditions);
            for(MedicalCondition MC : this.medicalConditions)
            {
                listModel.addElement(MC);
            }
        }
        list = new JList<Object>(listModel);
		
        JPanel medView = new JPanel(new MigLayout(new LC().fill(), new AC().grow(), new AC().grow()));
        medView.add(new JScrollPane(list), new CC().growX().wrap());
        
        //Add Buttons
        this.addButtonFld = new JButton("Add");
        //Splits the cell into 3
        medView.add(addButtonFld, new CC().split(3));
        
        this.delButtonFld = new JButton("X");
        this.delButtonFld.setForeground(Color.RED);
        medView.add(this.delButtonFld);
        
        
        //Add ComboBox
        this.comboBox = new JComboBox<Object>(medConds);
        AutoCompleteDecorator.decorate(this.comboBox);
        
        medView.add(this.comboBox,  new CC().growX());
        
        this.add(medView);
        this.pack();
        
        //Aligns text in center of list
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        
        Component parentComp = ((Component)parent);
        
        this.setLocation(parentComp.getX(), parentComp.getY());
        this.list = list;
        this.parent = parent;
       
        
        list.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    System.out.println("Clicking the list");
                    System.out.println(list.getSelectedIndex());
                }
         });
        
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.out.println("Closing window");
                //send a message to the PareitnContainer.
                
                //Skas says:
                //refer to variable _this, since 'this' in this scope, refers
                //to the anonymous function. Got a strange compiler error.
                //Apparently anything with 'className$1',where 1 can be any int,
                //refers to an anonymous class.
                parent.broadcast(_this);
                ((Component)parent).setEnabled(true);
                
            }

        });
        this.addButtonFld.addActionListener(this);
        this.addButtonFld.setActionCommand("Add");
        this.delButtonFld.addActionListener(this);
        this.delButtonFld.setActionCommand("Del");
       
        this.setVisible(true);
    }
   
    public ArrayList<MedicalCondition> getMedicalConditions()
    {
        ArrayList<MedicalCondition> listPatMedConds = new ArrayList<>();
        listPatMedConds.addAll(this.medicalConditions);
        return listPatMedConds;
    }
    @Override
    public void actionPerformed(ActionEvent e) 
    {
//                if(list.getRowS)
        switch(e.getActionCommand())
        {
            case "Add": 
                System.out.println("adding");
                if(!this.listModel.contains(comboBox.getSelectedItem()))
                {
                    this.listModel.addElement(comboBox.getSelectedItem());
                    this.medicalConditions.add((MedicalCondition)comboBox.getSelectedItem());
                    System.out.println("After add: "+ this.medicalConditions);

                }
            break;
            case "Del":
                int selectedInd = list.getSelectedIndex();
                if(selectedInd != -1)
                {
                    this.medicalConditions.remove(list.getSelectedValue());
                    this.listModel.remove(list.getSelectedIndex());
                    System.out.println("After remove: "+ this.medicalConditions);
                }
            break;
        }
    }
    public void keyPressed(KeyEvent event) {
     switch (event.getKeyCode()) {
         case KeyEvent.VK_UP:
             System.out.println("Up Selected");
             break;
         case KeyEvent.VK_DOWN:
             System.out.println("Down Selected");
             break;
         case KeyEvent.VK_RIGHT:
             // right arrow
             break;
         case KeyEvent.VK_LEFT:
             // left arrow
             break;
     }
}
       public static void main(String [] args)
    {
        GPSISDataMapper.connectToDatabase();
        ArrayList<MedicalCondition> mC = new ArrayList<>();
        new ViewMedicalCondition(new AddPatient(),mC); 
    }
}
