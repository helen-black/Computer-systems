/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author helen
 */
import MainLogic.MainLogic;
import java.awt.Color;
import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.table.TableModel;
public class NewJFrame extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame
     */
    public NewJFrame() {
        initComponents();
    }
    
    private ArrayList<Integer> makeStrInteger(String numProc) {
        ArrayList<Integer> arr = new ArrayList();
        if (!numProc.isEmpty()) {
            String[] strings = numProc.split(" ");
            for (String string : strings) 
                arr.add(Integer.parseInt(string));
        }
        return arr;
    }
    
    private Processor getMostPowerfulFreeForTask(Task task) {
        Processor res = new Processor();
        for(Processor proc: procs)
            if(!proc.isBusy() && proc.getPower() > res.getPower() &&
                    task.isProcValid(proc))
                res = proc;
        return res;        
    }
    
    //isBusy set to true of the res Processor 
    //if there's no free processor was found
    private Processor getMostPowerfulFreeProc(ArrayList<Processor> alreadyChosenProcs) {
        Processor res = new Processor();
        for(Processor proc: procs)
            if(!proc.isBusy() && proc.getPower() > res.getPower() &&
                    !alreadyChosenProcs.contains(proc))
                res = proc;
        return res;     
    }
    
    private Task getMostPowerfulSuitableTask(Processor proc) {
        Task res = new Task();
        boolean isValidProc = false;
        
        for(Task task: queue) {
            isValidProc = false;
            for(Integer id: task.getProcessorsId())
                if (id == proc.getId()) {
                    isValidProc = true;
                    break;
                }
            if(isValidProc && (task.getComplexity() > res.getComplexity()))
                res = task;
        }
        return res;
    }
    
    private void formAvailableTasks() {
        tasks = new ArrayList();
        TableModel model = tabTasks.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                ArrayList<Integer> arr = makeStrInteger((String)model.getValueAt(i, 0));
                double posibility = (double)model.getValueAt(i, 1);
                int complexity = (int)model.getValueAt(i, 2);

                tasks.add(new Task(complexity, posibility, arr));
            }
            catch(NullPointerException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    private void initProcs() {
        procs = new ArrayList();
        procs.add(new Processor(Integer.parseInt(jLabel11.getText()), 1));
        procs.add(new Processor(Integer.parseInt(jLabel14.getText()), 2));
        procs.add(new Processor(Integer.parseInt(jLabel13.getText()), 3));
        procs.add(new Processor(Integer.parseInt(jLabel15.getText()), 4));
        procs.add(new Processor(Integer.parseInt(jLabel16.getText()), 5));
        
    }
    
    private void makeProcsCalculation() {
        for (Processor currProc: procs) {
                currProc.makeCalculation();
            }
    }
    
    private float calculateKKD() {
        int OperCompleted = 0;
        int OperPossible = 0;
        for (Processor proc: procs) {
            OperCompleted += proc.getGeneralCompleted();
            OperPossible += proc.getPower() * ITERATIONS;
        }
        return (float)OperCompleted / (float)OperPossible * 100;
    }
    
    private float calculateKKD2(Processor scheduler) {
        int OperCompleted = 0;
        int OperPossible = 0;
        for (Processor proc: procs) {
            if (!proc.equals(scheduler)) {
                OperCompleted += proc.getGeneralCompleted();
                OperPossible += proc.getPower() * ITERATIONS;
            }
        }
        return (float)OperCompleted / (float)OperPossible * 100;
    }
    
    private void fillTxtFields(Processor scheduler) {
        for (Processor proc: procs) {
            switch(proc.getId()) {
                case 1:
                    txtEfficiencyProc1.setText(Integer.toString(proc.getGeneralCompleted()));
                    break;
                case 2:
                    txtEfficiencyProc2.setText(Integer.toString(proc.getGeneralCompleted()));
                    break;
                case 3:
                    txtEfficiencyProc3.setText(Integer.toString(proc.getGeneralCompleted()));
                    break;
                case 4:
                    txtEfficiencyProc4.setText(Integer.toString(proc.getGeneralCompleted()));
                    break;
                case 5:
                    txtEfficiencyProc5.setText(Integer.toString(proc.getGeneralCompleted()));
                    break;
            }
            txtLoopsPerformed.setText(Integer.toString(tasksGeneratedNum));
            txtKKD.setText(String.format("%.2f%s", calculateKKD(), "%"));
            txtKKD2.setText(String.format("%.2f%s", calculateKKD2(scheduler), "%"));
        }
    }
    
    private void addTasksToQueue() {
        for(Task task: tasks) {      //add new tasks to queue every 1 ms
            if (task.isAllowed()) {
                tasksGeneratedNum+=task.getComplexity();
                queue.addLast(task);
            }
        }
    }
    
    private Processor getMostWeekProc() {
        Processor res = procs.get(0);
        for (Processor proc: procs) {
            if (res.getPower() >= proc.getPower())
                res = proc;
        }
        return res;
    }
    
    private Processor getMostPowerfulProc() {
        Processor res = procs.get(0);
        for (Processor proc: procs) {
            if (res.getPower() < proc.getPower())
                res = proc;
        }
        return res;
    }
    
    private void runAlgFIFO() {
        tasksGeneratedNum = 0;
        
        initProcs();
        formAvailableTasks();
        
        queue = new LinkedList();
        
        for(int iteration = 0; iteration < ITERATIONS; iteration++) {
            addTasksToQueue();
            
            if (!queue.isEmpty()) {
                Processor proc;
                try {
                    while(true) {
                        Task task = queue.getFirst();
                        proc = getMostPowerfulFreeForTask(task);
                        
                        if(!proc.isBusy()) {         //add next task to completing
                            proc.setLeftToCalc(task.getComplexity());
                            queue.removeFirst();
                            continue;
                        }
                        break;
                    }
                } catch(NoSuchElementException e) {}
            }
            
            makeProcsCalculation();
        }
        
        fillTxtFields(null);
    }
    
    private void setInactive(Processor proc) {
        switch(proc.getId()) {
            case 1:
                lblProc1.setForeground(Color.red);
                break;
            case 2:
                lblProc2.setForeground(Color.red);
                break;
            case 3:
                lblProc3.setForeground(Color.red);
                break;
            case 4:
                lblProc4.setForeground(Color.red);
                break;
            case 5:
                lblProc5.setForeground(Color.red);
                break;
        }    
    }
    
    private void runAlgSepScheduler() {
        tasksGeneratedNum = 0;
        initProcs();
        formAvailableTasks();
        
        Processor scheduler = getMostWeekProc();
        procs.remove(scheduler);
        setInactive(scheduler);
        
        queue = new LinkedList();
        
        for(int iteration = 0; iteration < ITERATIONS; iteration++) {
            addTasksToQueue();
            
            if (!queue.isEmpty()) {
                Processor proc;
                try {
                    while(true) {
                        ArrayList<Processor> alreadyChosenProcs = new ArrayList();
                        proc = getMostPowerfulFreeProc(alreadyChosenProcs);
                        alreadyChosenProcs.add(proc);
                        
                        if(!proc.isBusy()) {         //add next task to completing
                            Task task = getMostPowerfulSuitableTask(proc);
                            if (task.getComplexity() != 0) {
                                proc.setLeftToCalc(task.getComplexity());
                                queue.remove(task);
                                continue;
                            }
                        }
                        break;
                    }
                } catch(NoSuchElementException e) {}
            }
            
            makeProcsCalculation();
        }
        procs.add(scheduler);
        fillTxtFields(scheduler);
        
    }
    
    private void runAlgPowScheduler(int rulingTime, int execTime) {
        tasksGeneratedNum = 0;
        initProcs();
        formAvailableTasks();
        
        Processor scheduler = getMostPowerfulProc();
        setInactive(scheduler);
        
        queue = new LinkedList();
        
        int scheduler_timer = 0;
        for(int iteration = 0; iteration < ITERATIONS; iteration++) {
            addTasksToQueue();
            
            if (scheduler_timer == 0) 
                procs.remove(scheduler);
            else if (scheduler_timer == rulingTime)
                    procs.add(scheduler);
            else if (scheduler_timer == execTime + rulingTime)
                    scheduler_timer = -1;
            scheduler_timer++;
            
            if (!queue.isEmpty() && scheduler_timer < 4) {
                Processor proc;
                try {
                    while(true) {
                        ArrayList<Processor> alreadyChosenProcs = new ArrayList();
                        proc = getMostPowerfulFreeProc(alreadyChosenProcs);
                        alreadyChosenProcs.add(proc);
                        
                        if(!proc.isBusy()) {         //add next task to completing
                            Task task = getMostPowerfulSuitableTask(proc);
                            if (task.getComplexity() != 0) {
                                proc.setLeftToCalc(task.getComplexity());
                                queue.remove(task);
                                continue;
                            }
                        }
                        break;
                    }
                } catch(NoSuchElementException e) {}
            }
            
            makeProcsCalculation();
        }
        if (scheduler_timer > 4 && scheduler_timer == 0)
            procs.add(scheduler);
        fillTxtFields(scheduler);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        boxAlgNum = new javax.swing.JComboBox<>();
        btnStart = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        scrollTasksTable = new javax.swing.JScrollPane();
        tabTasks = new javax.swing.JTable();
        sliderProc1 = new javax.swing.JSlider();
        sliderProc2 = new javax.swing.JSlider();
        sliderProc3 = new javax.swing.JSlider();
        sliderProc4 = new javax.swing.JSlider();
        sliderProc5 = new javax.swing.JSlider();
        txtLoopsPerformed = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtKKD = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtKKD2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtEfficiencyProc1 = new javax.swing.JTextField();
        txtEfficiencyProc2 = new javax.swing.JTextField();
        txtEfficiencyProc3 = new javax.swing.JTextField();
        txtEfficiencyProc4 = new javax.swing.JTextField();
        txtEfficiencyProc5 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        lblProc1 = new javax.swing.JLabel();
        lblProc2 = new javax.swing.JLabel();
        lblProc3 = new javax.swing.JLabel();
        lblProc4 = new javax.swing.JLabel();
        lblProc5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtPowerUser = new javax.swing.JTextField();
        txtPowerUser2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        boxAlgNum.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FIFO", "Separate Scheduler", "Powerful Scheduler" }));
        boxAlgNum.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                boxAlgNumItemStateChanged(evt);
            }
        });
        boxAlgNum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boxAlgNumActionPerformed(evt);
            }
        });

        btnStart.setFont(new java.awt.Font("Noto Sans", 1, 15)); // NOI18N
        btnStart.setForeground(new java.awt.Color(45, 162, 45));
        btnStart.setText("Start");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        btnClear.setFont(new java.awt.Font("Noto Sans", 1, 15)); // NOI18N
        btnClear.setForeground(new java.awt.Color(180, 92, 211));
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        tabTasks.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "№p", "P", "N"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        scrollTasksTable.setViewportView(tabTasks);

        sliderProc1.setMajorTickSpacing(5);
        sliderProc1.setMinimum(10);
        sliderProc1.setMinorTickSpacing(5);
        sliderProc1.setValue(10);
        sliderProc1.setName(""); // NOI18N
        sliderProc1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderProc1StateChanged(evt);
            }
        });

        sliderProc2.setMajorTickSpacing(5);
        sliderProc2.setMinimum(10);
        sliderProc2.setMinorTickSpacing(5);
        sliderProc2.setValue(10);

        sliderProc3.setMajorTickSpacing(5);
        sliderProc3.setMinimum(10);
        sliderProc3.setMinorTickSpacing(5);
        sliderProc3.setValue(10);

        sliderProc4.setMajorTickSpacing(5);
        sliderProc4.setMinimum(10);
        sliderProc4.setMinorTickSpacing(5);
        sliderProc4.setValue(10);

        sliderProc5.setMajorTickSpacing(5);
        sliderProc5.setMinimum(10);
        sliderProc5.setMinorTickSpacing(5);
        sliderProc5.setValue(10);

        txtLoopsPerformed.setEditable(false);
        txtLoopsPerformed.setText("0");

        jLabel1.setText("Кількість операцій за 10 с");

        jLabel2.setText("ККД");

        txtKKD.setEditable(false);

        jLabel3.setText("ККД'");

        txtKKD2.setEditable(false);

        jLabel4.setText("Кількість операцій за 10 с");

        txtEfficiencyProc1.setEditable(false);
        txtEfficiencyProc1.setText("0");
        txtEfficiencyProc1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEfficiencyProc1ActionPerformed(evt);
            }
        });

        txtEfficiencyProc2.setEditable(false);
        txtEfficiencyProc2.setText("0");
        txtEfficiencyProc2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEfficiencyProc2ActionPerformed(evt);
            }
        });

        txtEfficiencyProc3.setEditable(false);
        txtEfficiencyProc3.setText("0");

        txtEfficiencyProc4.setEditable(false);
        txtEfficiencyProc4.setText("0");

        txtEfficiencyProc5.setEditable(false);
        txtEfficiencyProc5.setText("0");

        jLabel5.setText("Задачі та ймовірності їх появи");

        lblProc1.setText("Процесор 1");

        lblProc2.setText("Процесор 2");

        lblProc3.setText("Процесор 3");

        lblProc4.setText("Процесор 4");

        lblProc5.setText("Процесор 5");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sliderProc1, org.jdesktop.beansbinding.ELProperty.create("${value}"), jLabel11, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sliderProc3, org.jdesktop.beansbinding.ELProperty.create("${value}"), jLabel13, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sliderProc2, org.jdesktop.beansbinding.ELProperty.create("${value}"), jLabel14, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sliderProc4, org.jdesktop.beansbinding.ELProperty.create("${value}"), jLabel15, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sliderProc5, org.jdesktop.beansbinding.ELProperty.create("${value}"), jLabel16, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtPowerUser.setText("4");
        txtPowerUser.setEnabled(false);

        txtPowerUser2.setText("20");
        txtPowerUser2.setEnabled(false);

        jLabel6.setText("Планування");

        jLabel7.setText("Виконання");

        jLabel8.setText("Алгоритм");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel1)
                        .addGap(116, 116, 116)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addGap(147, 147, 147))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrollTasksTable, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sliderProc5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(sliderProc4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(lblProc2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel14))
                                .addComponent(sliderProc2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(lblProc5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel16))
                                .addComponent(sliderProc1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(lblProc3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel13))
                                .addComponent(sliderProc3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(lblProc4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel15))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(7, 7, 7)
                                    .addComponent(lblProc1)
                                    .addGap(18, 18, Short.MAX_VALUE)
                                    .addComponent(jLabel11))))
                        .addGap(59, 59, 59)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtEfficiencyProc2)
                            .addComponent(txtEfficiencyProc1)
                            .addComponent(txtEfficiencyProc3)
                            .addComponent(txtEfficiencyProc4)
                            .addComponent(txtEfficiencyProc5, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(txtLoopsPerformed, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(146, 146, 146)
                        .addComponent(txtKKD, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(96, 96, 96)
                        .addComponent(txtKKD2, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(131, 131, 131))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addGap(34, 34, 34))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(348, 348, 348)
                                .addComponent(txtPowerUser2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(boxAlgNum, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(25, 25, 25)
                                        .addComponent(txtPowerUser, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7)))
                        .addGap(221, 262, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(486, Short.MAX_VALUE)
                    .addComponent(jLabel12)
                    .addGap(200, 200, 200)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(boxAlgNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPowerUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPowerUser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblProc1)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtEfficiencyProc1)
                            .addComponent(sliderProc1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblProc2)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEfficiencyProc2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sliderProc2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblProc3)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(sliderProc3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEfficiencyProc3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblProc4)
                                    .addComponent(jLabel15))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sliderProc4, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtEfficiencyProc4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblProc5)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sliderProc5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEfficiencyProc5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollTasksTable, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtKKD2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4))
                    .addComponent(txtLoopsPerformed, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtKKD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(125, 125, 125)
                    .addComponent(jLabel12)
                    .addContainerGap(480, Short.MAX_VALUE)))
        );

        bindingGroup.bind();

        setSize(new java.awt.Dimension(696, 633));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void boxAlgNumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boxAlgNumActionPerformed
         ComboBoxModel box = boxAlgNum.getModel();
        if(box.getSelectedItem() == "Powerful Scheduler") {
            txtPowerUser.setEnabled(true);
            txtPowerUser2.setEnabled(true);        
        }
        else
        {
            txtPowerUser.setEnabled(false);
            txtPowerUser2.setEnabled(false); 
        }
// TODO add your handling code here:
    }//GEN-LAST:event_boxAlgNumActionPerformed

    private void boxAlgNumItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_boxAlgNumItemStateChanged
        // TODO add your handling code here:
        
    }//GEN-LAST:event_boxAlgNumItemStateChanged

    private void txtEfficiencyProc2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEfficiencyProc2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEfficiencyProc2ActionPerformed

    private void sliderProc1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderProc1StateChanged
        //threadVal1.setText(Integer.toString(jSlider1.getValue()));
    }//GEN-LAST:event_sliderProc1StateChanged

    private void txtEfficiencyProc1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEfficiencyProc1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEfficiencyProc1ActionPerformed

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        ComboBoxModel box = boxAlgNum.getModel();
        if(box.getSelectedItem() == "FIFO")
            runAlgFIFO();
        else 
            if(box.getSelectedItem() == "Separate Scheduler") {
                runAlgSepScheduler();
            }
            else {
                runAlgPowScheduler(Integer.parseInt(txtPowerUser.getText()), 
                        Integer.parseInt(txtPowerUser2.getText()));
            }
    }//GEN-LAST:event_btnStartActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        txtEfficiencyProc1.setText("0");
        txtEfficiencyProc2.setText("0");
        txtEfficiencyProc3.setText("0");
        txtEfficiencyProc4.setText("0");
        txtEfficiencyProc5.setText("0");
        
        txtLoopsPerformed.setText("0");
        txtKKD.setText("0");
        txtKKD2.setText("0");
        
        lblProc1.setForeground(Color.black);
        lblProc2.setForeground(Color.black);
        lblProc3.setForeground(Color.black);
        lblProc4.setForeground(Color.black);
        lblProc5.setForeground(Color.black);
    }//GEN-LAST:event_btnClearActionPerformed

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame().setVisible(true);
            }
        });
    }
    
    private int tasksGeneratedNum;
    private ArrayList<Task> tasks;
    private ArrayList<Processor> procs;
    private LinkedList<Task>  queue;
    
    final private int ITERATIONS = 10000;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> boxAlgNum;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnStart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel lblProc1;
    private javax.swing.JLabel lblProc2;
    private javax.swing.JLabel lblProc3;
    private javax.swing.JLabel lblProc4;
    private javax.swing.JLabel lblProc5;
    private javax.swing.JScrollPane scrollTasksTable;
    private javax.swing.JSlider sliderProc1;
    private javax.swing.JSlider sliderProc2;
    private javax.swing.JSlider sliderProc3;
    private javax.swing.JSlider sliderProc4;
    private javax.swing.JSlider sliderProc5;
    private javax.swing.JTable tabTasks;
    private javax.swing.JTextField txtEfficiencyProc1;
    private javax.swing.JTextField txtEfficiencyProc2;
    private javax.swing.JTextField txtEfficiencyProc3;
    private javax.swing.JTextField txtEfficiencyProc4;
    private javax.swing.JTextField txtEfficiencyProc5;
    private javax.swing.JTextField txtKKD;
    private javax.swing.JTextField txtKKD2;
    private javax.swing.JTextField txtLoopsPerformed;
    private javax.swing.JTextField txtPowerUser;
    private javax.swing.JTextField txtPowerUser2;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
