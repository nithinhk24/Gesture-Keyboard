namespace Gesture_Typing
{
    partial class Ribbon1 : Microsoft.Office.Tools.Ribbon.RibbonBase
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        public Ribbon1()
            : base(Globals.Factory.GetRibbonFactory())
        {
            InitializeComponent();
        }

        /// <summary> 
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Component Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.tab1 = this.Factory.CreateRibbonTab();
            this.group1 = this.Factory.CreateRibbonGroup();
            this.buttonStart = this.Factory.CreateRibbonButton();
            this.buttonStop = this.Factory.CreateRibbonButton();
            this.tab1.SuspendLayout();
            this.group1.SuspendLayout();
            this.SuspendLayout();
            // 
            // tab1
            // 
            this.tab1.ControlId.ControlIdType = Microsoft.Office.Tools.Ribbon.RibbonControlIdType.Office;
            this.tab1.Groups.Add(this.group1);
            this.tab1.Label = "TabAddIns";
            this.tab1.Name = "tab1";
            // 
            // group1
            // 
            this.group1.Items.Add(this.buttonStart);
            this.group1.Items.Add(this.buttonStop);
            this.group1.Label = "Gesture typing";
            this.group1.Name = "group1";
            // 
            // buttonStart
            // 
            this.buttonStart.ControlSize = Microsoft.Office.Core.RibbonControlSize.RibbonControlSizeLarge;
            this.buttonStart.Image = global::Gesture_Typing.Properties.Resources.ok_mark_clip_art;
            this.buttonStart.KeyTip = "S";
            this.buttonStart.Label = "Start";
            this.buttonStart.Name = "buttonStart";
            this.buttonStart.ShowImage = true;
            this.buttonStart.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.buttonStart_Click);
            // 
            // buttonStop
            // 
            this.buttonStop.ControlSize = Microsoft.Office.Core.RibbonControlSize.RibbonControlSizeLarge;
            this.buttonStop.Description = "Closes the opened serial port";
            this.buttonStop.Enabled = false;
            this.buttonStop.Image = global::Gesture_Typing.Properties.Resources.cancel_mark;
            this.buttonStop.KeyTip = "C";
            this.buttonStop.Label = "Stop";
            this.buttonStop.Name = "buttonStop";
            this.buttonStop.ShowImage = true;
            this.buttonStop.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.buttonStop_Click);
            // 
            // Ribbon1
            // 
            this.Name = "Ribbon1";
            this.RibbonType = "Microsoft.Word.Document";
            this.Tabs.Add(this.tab1);
            this.Load += new Microsoft.Office.Tools.Ribbon.RibbonUIEventHandler(this.Ribbon1_Load);
            this.tab1.ResumeLayout(false);
            this.tab1.PerformLayout();
            this.group1.ResumeLayout(false);
            this.group1.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        internal Microsoft.Office.Tools.Ribbon.RibbonTab tab1;
        internal Microsoft.Office.Tools.Ribbon.RibbonGroup group1;
        internal Microsoft.Office.Tools.Ribbon.RibbonButton buttonStart;
        internal Microsoft.Office.Tools.Ribbon.RibbonButton buttonStop;
    }

    partial class ThisRibbonCollection
    {
        internal Ribbon1 Ribbon1
        {
            get { return this.GetRibbon<Ribbon1>(); }
        }
    }
}
