<template>
    <div>
      <!--弹出的对话框-->
      <v-dialog max-width="500" v-model="show" persistent>
        <v-card>
          <!--对话框的标题-->
          <v-toolbar dense dark color="primary">
            <v-toolbar-title>新增品牌</v-toolbar-title>
            <v-spacer/>
            <!--关闭窗口的按钮-->
            <v-btn icon @click="closeWindow"><v-icon>close</v-icon></v-btn>
          </v-toolbar>
          <!--对话框的内容，表单-->
          <v-card-text class="px-5">
            <my-brand-form @reload="reload" v-bind:isEdit="isEdit" v-bind:oldBrand="oldBrand"></my-brand-form>
          </v-card-text>
        </v-card>
      </v-dialog>

<!--      &lt;!&ndash;弹出的对话框&ndash;&gt;
      <v-dialog v-model="show" scrollable persistent max-width="500px">
        <v-card color="blue lighten-4" class="white&#45;&#45;text">
          <v-card-title>
            <span class="headline">{{isEdit?'修改':'新增'}}品牌</span>
            <v-spacer></v-spacer>
            <v-btn icon @click="show = false">
              <v-icon>close</v-icon>
            </v-btn>
          </v-card-title>
          <v-card-text class="px-5">
            <my-brand-form @reload="reload" v-bind:isEdit="isEdit" v-bind:oldBrand="oldBrand"></my-brand-form>
          </v-card-text>
        </v-card>
      </v-dialog>-->

      <!--可以用style="padding"-->
      <v-layout class="px-3 pb-2">
        <v-flex xs2 sm1 md1>
        <v-btn color="grey" @click="addBrand">新增品牌</v-btn>
      </v-flex>
        <!--撑开-->
        <v-spacer></v-spacer>
        <v-flex xs4 sm2 md2>
          <!--hide-details隐藏提示-->
          <v-text-field label="搜索" hide-details append-icon="search" v-model="cm_search"></v-text-field>
        </v-flex>
      </v-layout>

      <!--pagination-->
      <v-data-table
        :headers="headers"
        :items="brands"
        :pagination.sync="pagination"
        :total-items="totalBrands"
        :loading="loading"
        class="elevation-1"
      >
        <!--遍历items(这里是brands) 渲染items里面的数据 把每一个item传给props-->
        <template slot="items" slot-scope="props">
          <td class="text-xs-center">{{ props.item.id }}</td>
          <td class="text-xs-center">{{ props.item.name }}</td>
          <td class="text-xs-center"><img :src="props.item.image" alt=""></td>
          <td class="text-xs-center">{{ props.item.letter }}</td>
          <td class="text-xs-center">
            <v-btn flat icon color="grey">
              <v-icon>edit</v-icon>
            </v-btn>

            <v-btn flat icon color="error">
              <v-icon>delete</v-icon>
            </v-btn>
          </td>
        </template>
      </v-data-table>
    </div>
</template>

<script>
  import MyBrandForm from './MyBrandForm'
    export default {
        name: "MyBrand",
      data(){
          return{
            headers:[
              {
                text: '品牌id',align: 'center',sortable: true,value: 'id'},
              /*sorttable是否排序*/
              { text: '品牌名称',align:'center',sortable:false,value: 'name'},
              { text: '品牌LOGO',align:'center',sortable:false,value: 'image'},
              { text: '品牌首字母',align:'center',sortable:true,value: 'letter'},
              { text: '操作',align:'center',sortable:false}
            ],
            brands:[],
            pagination:{
              deep:true,//深层监控
              handler(){
                this.loadBrands();
              }
            },
            totalBrands:0,
            loading:false,
            cm_search:"",//查询条件
            show:false,  //控制对话框的显示
            oldBrand:{}, //回显要修改的数据
            isEdit:false, //是否编辑
            selected:[] //选择的条目
          }
      },
      /*在页面初始化的时候给brands赋值*/
      created() {
/*        this.brands=[
          {id:1,name:"小米",image:"",letter:"0"}
        ];*/

        this.totalBrands=15;
        this.loadBrands();
      },
      /*监控查询数据，如果变动就查询*/
      watch:{
        cm_search(){
          /*查询时页码也要改,页码改了，查询也会触发*/
          this.pagination.page=1;
          //this.loadBrands();
        },
        pagination:{
          deep:true,
          handler() {
            this.loadBrands();
          }
        }
      },
      methods:{
        loadBrands(){
          this.loading=false;
          //axios配置文件里的baseURL自动加在url的前面
          this.$http.get("/item/brand/page",{
            params:{
              //搜索条件
              key:this.cm_search,//查询条件
              /*插件自动生成*/
              page:this.pagination.page,//当前页
              rows:this.pagination.rowsPerPage,//每页大小
              sortBy:this.pagination.sortBy,//排序方式
              desc:this.pagination.descending,//是否降序

            }
          }).then(resp=>{
            this.brands=resp.data.items;
            this.totalBrands=resp.data.data.total;
            this.loading=true;
          })
        },
        reload(){
          //关闭对话框
          this.show=false;
          //刷新页面
          this.getDataFromServer();
        },
        addBrand(){
          // 控制弹窗可见：
          this.show = true;
        },
        editBrand(oldBrand){
          //根据品牌信息查询商品分类
          this.verify().then(() => {
            this.$http.get("/item/category/bid/"+oldBrand.id).then(
              ({data}) => {
                this.isEdit=true;
                //显示弹窗
                this.show=true;
                //获取要编辑的brand
                this.oldBrand=oldBrand;
                this.oldBrand.categories = data;
              }
            ).catch();
          }).catch(() => {
            //this.$router.push("/login");
          });

        },
        deleteBrand(oldBrand){
          if (this.selected.length === 1 && this.selected[0].id === oldBrand.id) {
            this.verify().then(() => {
              this.$message.confirm('此操作将永久删除该品牌, 是否继续?').then(
                () => {
                  //发起删除请求，删除单条数据
                  this.$http.delete("/item/brand/bid/" + oldBrand.id).then(() => {
                    this.getDataFromServer();
                  }).catch()
                }
              ).catch(() => {
                this.$message.info("删除已取消！");
              });
            }).catch(() => {
             // this.$router.push("/login");
            });
          }
        },
        deleteAllBrand(){
          //拼接id数组
          /**
           * 加了{}就必须有return
           * @type {any[]}
           */
          const ids = this.selected.map( s => s.id);
          //console.log(ids.length);
          if (ids.length > 0) {
            this.verify().then(() => {
              this.$message.confirm('此操作将永久删除所选品牌，是否继续?').then(
                () => {
                  this.$http.delete("/item/brand/bid/" + ids.join("-")).then(() => {
                    this.getDataFromServer();
                  }).catch();
                }
              ).catch(() => {
                this.$message.info("删除已取消！");
              });
            }).catch(() => {
              //this.$router.push("/login");
            });
          }
        },
        closeWindow(){
          // 关闭窗口
          this.show = false;
        }
      },components:{
        MyBrandForm
      }
    }
</script>

<style scoped>

</style>
