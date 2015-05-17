/**
 * Created by Jaime on 14/05/2015.
 */

var defaultTab = 'tab_menu';
var tabs = {};
var currentTab;
var techInfo;
var sampleCount;
var sampleRow;

var maxSamples = 10;
var minSamples = 2;

function TechnicalInformation(){
    var tmp = $($('.info_block')[0]);
    this.showButton = $('#techinfo_show_button');
    this.hideButton = $('#techinfo_hide_button');
    this.rowModel = $($($('#service_row_model')[0]).clone());
    this.infoParent = $(tmp.parent());
    this.infoModel = $(tmp.clone());
    this.techInfo = $('#technical_information');
    var self = this;
    tmp.remove();

    this.showTechInfo = function(){
        self.showButton.hide();
        self.techInfo.fadeIn();
        self.hideButton.show();
    };

    this.hideTechInfo = function(){
        self.hideButton.hide();
        self.techInfo.hide();
        self.showButton.show();
    };

    this.fillTechnicalInformation = function(data){
        $('.info_block').remove();
        var inf;
        $.each(data, function(key, value){

            inf = $(self.infoModel.clone());
            $('#insulinUnits').text(value.result);
            inf.find('.ti_method').text(value.method);
            inf.find('.ti_successful').text(value.successful);
            inf.find('.ti_versionCount').text(value.versionCount);
            inf.find('.ti_result').text(value.result);
            inf.find('.ti_runTime').text(value.runTime);

            var i;
            var details = $(inf.find('.service_row_table')[0]);
            $.each(value.versionResults, function (key, ivalue) {
                i = self.rowModel.clone();
                $(i).id = '';
                $($(i).find('.service_name')[0]).text(ivalue.uri);
                $($(i).find('.service_successful')[0]).text(ivalue.successful);
                $($(i).find('.service_result')[0]).text(ivalue.result);
                $($(i).find('.service_runtime')[0]).text((ivalue.runTime)?ivalue.runTime:'Timeout');
                $(details).append(i);
                $(i).show();
            });
            self.infoParent.append(inf);
        });


    };

    this.showButton.on('click', self.showTechInfo);
    this.hideButton.on('click', self.hideTechInfo);
    this.showButton.show();
    this.hideButton.hide();

}

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

    this.presentResult = function(data, textStatus){
        log(data);
        techInfo.fillTechnicalInformation(data);
        techInfo.hideTechInfo();
        if(data[data.length-1].successful===true) selectTab('tab_result');
        else selectTab('tab_error');
    };

    this.onError = function(jqXHR, textStatus, errorThrown ){
        selectTab('tab_error');
    };

    this.hide = function(){
        this.disableCalculation();
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
        button.off('click');
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
        self.disableCalculation();
        if(tab.hasForm()==false) return;
        var payload = $(tab.form).serialize();
        log(payload);
        selectTab('tab_waiting');

        var ajax = $.ajax({
            type: "POST",
            url: tab.form.action,
            data: payload,
            success: self.presentResult,
            error: self.onError,
            dataType: "json",
            timeout: 4000
        });
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

$( document ).ready(function() {
    $.each($('.tab'), function(key, value){if(value.id)  tabs[value.id] = new TabObject(value);});
    techInfo = new TechnicalInformation();
    sampleRow = $('.samples_row')[0];
    sampleCount = minSamples;
    selectTab(defaultTab);
});

