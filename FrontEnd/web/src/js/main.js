/**
 * Created by Jaime on 14/05/2015.
 */

var defaultTab = 'tab_menu';
var tabs = {};
var currentTab;
var sampleCount;
var sampleRow;

var maxSamples = 10;
var minSamples = 2;

function TabObject(tab){
    //Properties
    this.tab = tab;
    this.name = tab.id;
    this.form = $(tab).find('.calculator_form')[0];
    this.submitButton = $(this.form).find('.btn_submit')[0];
    this.enabled = true;
    var self = this;

    //Methods
    this.hasForm = function(){return !!this.form;};

    this.resetTab = function(){
        if(this.hasForm()){
            this.disableCalculation();
            this.form.reset();
        }
    };

    this.hide = function(){
        $(this.tab).hide();
        this.resetTab();
    };

    this.show = function(){
        this.resetTab();
        $(this.tab).fadeIn();
    };

    this.enableCalculation = function() {
        this.enabled = true;
        var button = $(this.submitButton);
        button.removeClass('button_disabled');
        button.addClass('button btn_green');
        button.on('click', self.submit);
    };

    this.disableCalculation = function() {
        this.enabled = false;
        var button = $(this.submitButton);
        button.removeClass('button btn_green');
        button.addClass('button_disabled');
        button.off('click');
    };


    this.submit = function(){
        var tab = self;
        if(tab.hasForm()==false) return;

        var payload = $(tab.form).serialize();
        log(tab.form.action);
    };

    // Constructor for forms
    this.handleKeyupOnInputs = function(){
        var tab = self;
        if(tab.hasForm()) {
            if (tab.form.checkValidity()) tab.enableCalculation();
            else tab.disableCalculation();
        }
    };


    if(this.hasForm()){
        $(this.form).submit(function() { return false; });
        $.each($(this.form).find('input'), function(key, value){
            $(value).on('keyup input', self.handleKeyupOnInputs);
        });
    }
}



function selectTab(tabName){
    var tab = tabs[tabName];
    if(!tab) return;
    if(currentTab) currentTab.hide();
    currentTab = tab;
    tab.show();
}

function log(text){
    console.log(text)
}

$( document ).ready(function() {
    $.each($('.tab'), function(key, value){if(value.id)  tabs[value.id] = new TabObject(value);});
    sampleRow = $('.samples_row')[0];
    sampleCount = minSamples;
    selectTab(defaultTab);
});


function addSample(){
    if(sampleCount >= maxSamples) return;
    var sample = $(sampleRow).find('.sample_block')[1];
    var newSample = $(sample).clone();
    $(newSample).find('input').val('');
    $(sampleRow).append(newSample);
    sampleCount++;
}

function removeSample(){
    if(sampleCount <= minSamples) return;
    $(sampleRow).find('.sample_block').last().remove();
    sampleCount--;
}

function resetSamples(){
    while(sampleCount>2) removeSample();
}